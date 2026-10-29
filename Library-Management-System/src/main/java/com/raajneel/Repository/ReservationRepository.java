package com.raajneel.Repository;

import com.raajneel.Domain.ReservationStatus;
import com.raajneel.Model.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT CASE WHEN COUNT(r)>0 THEN true ELSE false END FROM Reservation r "+
            "WHERE r.user.id = :userId AND r.book.id = :bookId "+
            "AND (r.status = 'PENDING' OR r.status = 'AVAILABLE')")
    boolean hasActiveReservation(@Param("userId") Long userId, @Param("bookId") Long bookId);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.user.id = :userId "+
            "AND (r.status = 'PENDING' OR r.status = 'AVAILABLE')")
    long countActiveReservationsByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.book.id = :bookId "+
            "AND r.status = 'PENDING'")
    long countPendingReservationsByBook(@Param("bookId") Long bookId);

    @Query("SELECT r FROM Reservation r WHERE "+
            "(:userId IS NULL OR r.user.id = :userId) AND "+
            "(:bookId IS NULL OR r.book.id = :bookId) AND "+
            "(:status IS NULL OR r.status = :status) AND "+
            "(:activeOnly = false OR (r.status = 'PENDING' OR r.status = 'AVAILABLE'))")
    Page<Reservation> searchReservationsWithFilters(
            @Param("userId") Long userId,
            @Param("bookId") Long bookId,
            @Param("status") ReservationStatus status,
            @Param("activeOnly") boolean activeOnly,
            Pageable pageable
    );
}
