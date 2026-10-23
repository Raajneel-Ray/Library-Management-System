package com.raajneel.Service.Implementation;

import com.raajneel.Domain.BookLoanStatus;
import com.raajneel.Domain.BookLoanType;
import com.raajneel.Exception.BookException;
import com.raajneel.Mapper.BookLoanMapper;
import com.raajneel.Model.Book;
import com.raajneel.Model.BookLoan;
import com.raajneel.Model.SubscriptionPlan;
import com.raajneel.Model.User;
import com.raajneel.Payload.DTO.BookLoanDTO;
import com.raajneel.Payload.DTO.SubscriptionDTO;
import com.raajneel.Payload.Request.BookLoanSearchRequest;
import com.raajneel.Payload.Request.CheckinRequest;
import com.raajneel.Payload.Request.CheckoutRequest;
import com.raajneel.Payload.Request.RenewRequest;
import com.raajneel.Payload.Response.PageResponse;
import com.raajneel.Repository.BookLoanRepository;
import com.raajneel.Repository.BookRepository;
import com.raajneel.Repository.SubscriptionPlanRepository;
import com.raajneel.Repository.UserRepository;
import com.raajneel.Service.BookLoanService;
import com.raajneel.Service.SubscriptionService;
import com.raajneel.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookLoanServiceImpl implements BookLoanService {

    private final BookLoanRepository bookLoanRepository;
    private final UserService  userService;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final BookRepository bookRepository;
    private final BookLoanMapper bookLoanMapper;


    @Override
    public BookLoanDTO checkoutBook(CheckoutRequest request) throws Exception {
        User user = userService.getCurrentUser();
        return checkoutBookForUser(user.getId(), request);
    }

    @Override
    public BookLoanDTO checkoutBookForUser(Long userId, CheckoutRequest request) throws Exception {
        // fetch the user details, active subscription of the user and book details
        User user = userRepository.findById(userId).orElseThrow(() -> new Exception("User not found"));
        SubscriptionDTO subscriptionDTO = subscriptionService.getUsersActiveSubscription(userId);
        SubscriptionPlan plan = subscriptionPlanRepository.findById(subscriptionDTO.getPlanId())
                .orElseThrow(() -> new Exception("Plan not found"));
        Book book = bookRepository.findById(request.getBookId()).orElseThrow(() -> new BookException("Book not found"));
        if(!book.getActive()){
            throw new BookException("Book is not available");
        }
        if(!book.isAvailableCopiesValid() || book.getAvailableCopies() <= 0) {
            throw new BookException("Book is not available");
        }
        // check if that book has any active checkout for the user
        if(bookLoanRepository.hasActiveCheckOut(userId, book.getId())) {
            throw new BookException("Book already has active checked out");
        }
        long activeCheckOuts = bookLoanRepository.countActiveBookLoansByUser(userId);
        int maxBooksAllowed = subscriptionDTO.getMaxBooksAllowed();

        if(activeCheckOuts >= maxBooksAllowed) {
            throw new Exception("You have reached the maximum allowed number of book loans");
        }

        long overdueCount = bookLoanRepository.countOverdueBookLoansByUser(userId);
        if(overdueCount > 0) {
            throw new Exception("First return old overdue book!");
        }

        BookLoan bookLoan = BookLoan
                .builder()
                .user(user)
                .book(book)
                .type(BookLoanType.CHECKOUT)
                .status(BookLoanStatus.CHECKED_OUT)
                .checkoutDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(
                        (request.getCheckoutDays() != null && request.getCheckoutDays() > 0)
                                ? request.getCheckoutDays()
                                : (plan.getMaxDaysPerBook() != null ? plan.getMaxDaysPerBook() : 14)))
                .renewalCount(0)
                .maxRenewals(2)
                .notes(request.getNotes())
                .isOverdue(false)
                .overdueDays(0)
                .build();
        book.setAvailableCopies(book.getAvailableCopies()-1);
        bookRepository.save(book);
        BookLoan savedBookLoan = bookLoanRepository.save(bookLoan);
        return bookLoanMapper.toDTO(savedBookLoan);
    }

    @Override
    public BookLoanDTO checkinBook(CheckinRequest request) throws Exception {
        // validate book loan exists
        BookLoan bookLoan = bookLoanRepository.findById(request.getBookLoanId())
                .orElseThrow(() -> new Exception("Book loan not found"));

        // check if book is already returned
        if(!bookLoan.isActive()) {
            throw new Exception("Book loan is not active");
        }
        // set return date
        bookLoan.setReturnDate(LocalDate.now());
        BookLoanStatus condition = request.getCondition();
        if(condition == null) {
            condition = BookLoanStatus.RETURNED;
        }
        bookLoan.setStatus(condition);

        // todo fine implementation
        bookLoan.setOverdueDays(0);
        bookLoan.setIsOverdue(false);
        bookLoan.setNotes(request.getNotes()!=null?request.getNotes():"book returned by user");

        //reconciling the inventory
        if(condition!=BookLoanStatus.LOST) {
            Book book = bookLoan.getBook();
            book.setAvailableCopies(book.getAvailableCopies()+1);
            bookRepository.save(book);
        }

        BookLoan savedBookLoan = bookLoanRepository.save(bookLoan);
        return bookLoanMapper.toDTO(savedBookLoan);
    }

    @Override
    public BookLoanDTO renewCheckout(RenewRequest renewRequest) throws Exception {
        BookLoan bookLoan=bookLoanRepository.findById(renewRequest.getBookLoanId())
                .orElseThrow(()->new Exception("Book Loan  not found"));
        if (!bookLoan.canRenew()){
            throw new BookException("Book cannot be renewed");
        }
        bookLoan.setDueDate(bookLoan.getDueDate().plusDays(renewRequest.getExtensionDay()));
        bookLoan.setRenewalCount(bookLoan.getRenewalCount()+1);
        bookLoan.setNotes(renewRequest.getNotes()!=null?renewRequest.getNotes():"book renewed by user");
        BookLoan savedBookLoan=bookLoanRepository.save(bookLoan);
        return bookLoanMapper.toDTO(savedBookLoan);
    }

    @Override
    public PageResponse<BookLoanDTO> getMyBookLoans(BookLoanStatus status, int page, int size) throws Exception {
        User currentUser=userService.getCurrentUser();
        Page<BookLoan> bookLoanPage;
        if (status!=null){
            Pageable pageable= PageRequest.of(page,size, Sort.by("dueDate").ascending());
            bookLoanPage=bookLoanRepository.findByStatusAndUser(status,currentUser,pageable);
        }
        else {
            Pageable pageable=PageRequest.of(page,size,Sort.by("createdAt").descending());
            bookLoanPage=bookLoanRepository.findByUserId(currentUser.getId(),pageable);
        }

        return convertToPageResponse(bookLoanPage);
    }

    @Override
    public PageResponse<BookLoanDTO> getBookLoans(BookLoanSearchRequest request) {
        Pageable pageable=createPageable(
                request.getPage(),
                request.getSize(),
                request.getSortBy(),
                request.getSortDirection()
        );
        Page<BookLoan> bookLoanPage;
        if (Boolean.TRUE.equals(request.getOverdueOnly())){
            bookLoanPage=bookLoanRepository.findOverdueBookLoans(LocalDate.now(),pageable);
        } else if (request.getUserId()!=null) {
            bookLoanPage=bookLoanRepository.findByUserId(request.getUserId(),pageable);
        } else if (request.getBookId()!=null) {
            bookLoanPage=bookLoanRepository.findByBookId(request.getBookId(),pageable);
        } else if (request.getStatus()!=null) {
            bookLoanPage=bookLoanRepository.findByStatus(request.getStatus(),pageable);
        } else if (request.getStartDate()!=null && request.getEndDate()!=null) {
            bookLoanPage=bookLoanRepository.findBookLoansByDateRange(
                    request.getStartDate(),
                    request.getEndDate(),
                    pageable
            );
        } else {
            bookLoanPage =bookLoanRepository.findAll(pageable);
        }
        return convertToPageResponse(bookLoanPage);
    }

    @Override
    public int updateOverdueBookLoan() {
        Pageable pageable=PageRequest.of(0,1000);
        Page<BookLoan> overduePage=bookLoanRepository.findOverdueBookLoans(LocalDate.now(),pageable);
        int updateCount=0;
        for (BookLoan bookLoan:overduePage.getContent()){
            if (bookLoan.getStatus()==BookLoanStatus.CHECKED_OUT){
                bookLoan.setStatus(BookLoanStatus.OVERDUE);
                bookLoan.setIsOverdue(true);
                int overdueDays=calculateOverdueDate(bookLoan.getDueDate(),LocalDate.now());
                bookLoan.setOverdueDays(overdueDays);
                bookLoanRepository.save(bookLoan);
                updateCount++;
            }
        }
        return updateCount;
    }

    private PageResponse<BookLoanDTO> convertToPageResponse(Page<BookLoan> bookLoanPage){
        List<BookLoanDTO> bookLoanDTOs=bookLoanPage.getContent().stream().map(bookLoanMapper::toDTO).collect(Collectors.toList());
        return new PageResponse<>(
                bookLoanDTOs,
                bookLoanPage.getNumber(),
                bookLoanPage.getSize(),
                (int) bookLoanPage.getTotalElements(),
                bookLoanPage.getTotalPages(),
                bookLoanPage.isLast(),
                bookLoanPage.isFirst(),
                bookLoanPage.isEmpty()
        );
    }

    private Pageable createPageable(int page, int size, String sortBy, String sortDirection){
        size=Math.min(size,100);
        size=Math.max(size,1);
        Sort sort=sortDirection.equalsIgnoreCase("ASC")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        return  PageRequest.of(page,size,sort);
    }

    public  int calculateOverdueDate(LocalDate dueDate,LocalDate today){
        if (today.isBefore(dueDate) || today.isEqual(dueDate)){
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(dueDate,today);
    }

}
