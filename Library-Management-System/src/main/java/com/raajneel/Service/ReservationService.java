package com.raajneel.Service;

import com.raajneel.Payload.DTO.ReservationDTO;
import com.raajneel.Payload.Request.ReservationRequest;
import com.raajneel.Payload.Request.ReservationSearchRequest;
import com.raajneel.Payload.Response.PageResponse;

public interface ReservationService {
    ReservationDTO createReservation(ReservationRequest reservationRequest) throws Exception;
    ReservationDTO createReservationForUser(ReservationRequest reservationRequest,Long userId) throws Exception;
    ReservationDTO cancelReservation(Long reservationId) throws Exception;
    ReservationDTO fulfillReservation(Long reservationId) throws Exception;
    PageResponse<ReservationDTO> getMyReservations(ReservationSearchRequest searchRequest) throws Exception;
    PageResponse<ReservationDTO> searchReservations(ReservationSearchRequest searchRequest);
}
