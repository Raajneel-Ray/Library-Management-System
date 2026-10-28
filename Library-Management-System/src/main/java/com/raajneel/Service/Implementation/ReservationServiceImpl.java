package com.raajneel.Service.Implementation;

import com.raajneel.Mapper.ReservationMapper;
import com.raajneel.Payload.DTO.ReservationDTO;
import com.raajneel.Payload.Request.ReservationRequest;
import com.raajneel.Payload.Request.ReservationSearchRequest;
import com.raajneel.Payload.Response.PageResponse;
import com.raajneel.Repository.BookLoanRepository;
import com.raajneel.Repository.BookRepository;
import com.raajneel.Repository.ReservationRepository;
import com.raajneel.Service.BookLoanService;
import com.raajneel.Service.ReservationService;
import com.raajneel.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final BookLoanRepository bookLoanRepository;
    private final UserService userService;
    private final BookRepository bookRepository;
    private  final ReservationRepository reservationRepository;
    private  final ReservationMapper reservationMapper;
    private  final BookLoanService bookLoanService;

    int MAX_RESERVATION = 5;

    @Override
    public ReservationDTO createReservation(ReservationRequest reservationRequest) throws Exception {
        return null;
    }

    @Override
    public ReservationDTO createReservationForUser(ReservationRequest reservationRequest, Long userId) throws Exception {

        return null;
    }

    @Override
    public ReservationDTO cancelReservation(Long reservationId) throws Exception {
        return null;
    }

    @Override
    public ReservationDTO fulfillReservation(Long reservationId) throws Exception {
        return null;
    }

    @Override
    public PageResponse<ReservationDTO> getMyReservations(ReservationSearchRequest searchRequest) throws Exception {
        return null;
    }

    @Override
    public PageResponse<ReservationDTO> searchReservations(ReservationSearchRequest searchRequest) {
        return null;
    }
}
