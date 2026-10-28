package com.raajneel.Model;

import com.raajneel.Domain.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Book book;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status=ReservationStatus.PENDING;

    private LocalDateTime reservedAt;

    private LocalDateTime availableAt;

    private LocalDateTime availableUntil;

    @Column(name ="fulfilled_at")
    private LocalDateTime fulfilledAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "queue_position")
    private Integer queuePosition;

    @Column(name ="notification_sent", nullable = false)
    private Boolean notificationSend=false;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public boolean canBeCancelled(){
        return status==ReservationStatus.PENDING || status == ReservationStatus.AVAILABLE;
    }

    public  boolean hasExpired(){
        return status==ReservationStatus.AVAILABLE && availableUntil != null && LocalDateTime.now().isAfter(availableUntil);
    }
}
