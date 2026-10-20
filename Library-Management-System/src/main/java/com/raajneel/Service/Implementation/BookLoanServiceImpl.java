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
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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
        if(book.isAvailableCopiesValid()) {
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
                        request.getCheckoutDays() < plan.getMaxDaysPerBook() ? plan.getMaxDaysPerBook(): request.getCheckoutDays()))
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
    public BookLoanDTO checkinBook(CheckinRequest request) {
        return null;
    }

    @Override
    public BookLoanDTO renewCheckout(RenewRequest renewRequest) {
        return null;
    }

    @Override
    public PageResponse<BookLoanDTO> getMyBookLoans(BookLoanStatus status, int page, int size) {
        return null;
    }

    @Override
    public PageResponse<BookLoanDTO> getBookLoans(BookLoanSearchRequest request) {
        return null;
    }

    @Override
    public int updateOverdueBookLoan() {
        return 0;
    }
}
