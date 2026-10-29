package com.raajneel.Service.Implementation;

import com.raajneel.Domain.BookLoanStatus;
import com.raajneel.Domain.ReservationStatus;
import com.raajneel.Domain.UserRole;
import com.raajneel.Mapper.ReservationMapper;
import com.raajneel.Model.Book;
import com.raajneel.Model.Reservation;
import com.raajneel.Model.User;
import com.raajneel.Payload.DTO.ReservationDTO;
import com.raajneel.Payload.Request.CheckoutRequest;
import com.raajneel.Payload.Request.ReservationRequest;
import com.raajneel.Payload.Request.ReservationSearchRequest;
import com.raajneel.Payload.Response.PageResponse;
import com.raajneel.Repository.BookLoanRepository;
import com.raajneel.Repository.BookRepository;
import com.raajneel.Repository.ReservationRepository;
import com.raajneel.Repository.UserRepository;
import com.raajneel.Service.BookLoanService;
import com.raajneel.Service.ReservationService;
import com.raajneel.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final BookLoanRepository bookLoanRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final BookLoanService bookLoanService;

    private static final int MAX_RESERVATION = 5;

    @Override
    public ReservationDTO createReservation(ReservationRequest reservationRequest) throws Exception {
        User user = userService.getCurrentUser();
        return createReservationForUser(reservationRequest, user.getId());
    }

    @Override
    public ReservationDTO createReservationForUser(ReservationRequest reservationRequest, Long userId) throws Exception {
        boolean alreadyHasLoan = bookLoanRepository.existsByUserIdAndBookIdAndStatus(
                userId, reservationRequest.getBookId(), BookLoanStatus.CHECKED_OUT
        );
        if (alreadyHasLoan) {
            throw new Exception("You already have loan on this book");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with ID: " + userId));

        Book book = bookRepository.findById(reservationRequest.getBookId())
                .orElseThrow(() -> new Exception("Book not Found"));

        if (reservationRepository.hasActiveReservation(userId, book.getId())) {
            throw new Exception("You already have active reservation on this book");
        }

        if (book.getAvailableCopies() > 0) {
            throw new Exception("Book is already available");
        }

        long activeReservations = reservationRepository.countActiveReservationsByUser(userId);
        if (activeReservations >= MAX_RESERVATION) {
            throw new Exception("You have reached maximum allowed active reservations (" + MAX_RESERVATION + ")");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setNotificationSend(false);
        reservation.setNotes(reservationRequest.getNotes());

        long pendingCount = reservationRepository.countPendingReservationsByBook(book.getId());
        reservation.setQueuePosition((int) pendingCount + 1);

        Reservation savedReservation = reservationRepository.save(reservation);
        return reservationMapper.toDTO(savedReservation);
    }

    @Override
    public ReservationDTO cancelReservation(Long reservationId) throws Exception {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new Exception("Reservation not found with ID: " + reservationId));

        User currentUser = userService.getCurrentUser();
        if (!reservation.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != UserRole.ROLE_ADMIN) {
            throw new Exception("You can only cancel your own reservations");
        }
        if (!reservation.canBeCancelled()) {
            throw new Exception("Reservation cannot be cancelled (current status: " + reservation.getStatus() + ")");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());

        Reservation savedReservation = reservationRepository.save(reservation);
        return reservationMapper.toDTO(savedReservation);
    }

    @Override
    public ReservationDTO fulfillReservation(Long reservationId) throws Exception {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new Exception("Reservation not found with ID : " + reservationId));

        if (reservation.getStatus() != ReservationStatus.AVAILABLE && reservation.getStatus() != ReservationStatus.PENDING) {
            throw new Exception("Reservation cannot be fulfilled (current status: " + reservation.getStatus() + ")");
        }

        if (reservation.getBook().getAvailableCopies() <= 0) {
            throw new Exception("Book has no available copies for pickup");
        }

        reservation.setStatus(ReservationStatus.FULFILLED);
        reservation.setFulfilledAt(LocalDateTime.now());

        Reservation savedReservation = reservationRepository.save(reservation);
        CheckoutRequest request = new CheckoutRequest();
        request.setBookId(reservation.getBook().getId());
        request.setNotes("Assign Booked by Admin");
        bookLoanService.checkoutBookForUser(reservation.getUser().getId(), request);
        return reservationMapper.toDTO(savedReservation);
    }

    @Override
    public PageResponse<ReservationDTO> getMyReservations(ReservationSearchRequest searchRequest) throws Exception {
        User user = userService.getCurrentUser();
        searchRequest.setUserId(user.getId());
        return searchReservations(searchRequest);
    }

    @Override
    public PageResponse<ReservationDTO> searchReservations(ReservationSearchRequest searchRequest) {
        Pageable pageable = createPageable(searchRequest);
        Page<Reservation> reservationPage = reservationRepository.searchReservationsWithFilters(
                searchRequest.getUserId(),
                searchRequest.getBookId(),
                searchRequest.getStatus(),
                searchRequest.getActiveOnly() != null ? searchRequest.getActiveOnly() : false,
                pageable
        );
        return builtPageResponse(reservationPage);
    }

    private PageResponse<ReservationDTO> builtPageResponse(Page<Reservation> reservationPage) {
        List<ReservationDTO> dtos = reservationPage.getContent().stream()
                .map(reservationMapper::toDTO)
                .toList();
        PageResponse<ReservationDTO> response = new PageResponse<>();
        response.setContent(dtos);
        response.setPageNumber(reservationPage.getNumber());
        response.setPageSize(reservationPage.getSize());
        response.setTotalElements((int) reservationPage.getTotalElements());
        response.setTotalPages(reservationPage.getTotalPages());
        response.setLast(reservationPage.isLast());
        response.setFirst(reservationPage.isFirst());
        response.setEmpty(reservationPage.isEmpty());

        return response;
    }

    private Pageable createPageable(ReservationSearchRequest searchRequest) {
        String sortBy = (searchRequest.getSortBy() != null && !searchRequest.getSortBy().isBlank())
                ? searchRequest.getSortBy()
                : "reservedAt";
        Sort sort = "ASC".equalsIgnoreCase(searchRequest.getSortDirection())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        int size = Math.max(1, Math.min(searchRequest.getSize(), 100));
        int page = Math.max(0, searchRequest.getPage());
        return PageRequest.of(page, size, sort);
    }
}
