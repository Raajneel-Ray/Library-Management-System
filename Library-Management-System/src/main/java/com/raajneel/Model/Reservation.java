package com.raajneel.Model;

import com.raajneel.Domain.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status = ReservationStatus.PENDING;

    private LocalDateTime reservedAt;

    private LocalDateTime availableAt;

    private LocalDateTime availableUntil;

    @Column(name = "fulfilled_at")
    private LocalDateTime fulfilledAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "queue_position")
    private Integer queuePosition;

    @Builder.Default
    @Column(name = "notification_sent", nullable = false)
    private Boolean notificationSend = false;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Alias getters/setters for notificationSent to maintain backwards compatibility
    public Boolean getNotificationSent() {
        return notificationSend;
    }

    public void setNotificationSent(Boolean notificationSent) {
        this.notificationSend = notificationSent;
    }

    public boolean canBeCancelled() {
        return (status == ReservationStatus.PENDING || status == ReservationStatus.AVAILABLE) && !hasExpired();
    }

    public boolean hasExpired() {
        return status == ReservationStatus.EXPIRED
                || (status == ReservationStatus.AVAILABLE && availableUntil != null && LocalDateTime.now().isAfter(availableUntil));
    }
}
