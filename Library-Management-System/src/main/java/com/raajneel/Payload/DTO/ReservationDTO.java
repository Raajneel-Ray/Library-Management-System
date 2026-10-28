package com.raajneel.Payload.DTO;

import com.raajneel.Domain.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationDTO {
    private Long id;

    private Long userId;
    private String userName;
    private String userEmail;

    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private  String bookAuthor;
    private Boolean isBookAvailable;

    private ReservationStatus status;

    private LocalDateTime reservedAt;

    private LocalDateTime availableAt;

    private LocalDateTime availableUntil;

    private LocalDateTime fulfilledAt;

    private LocalDateTime cancelledAt;

    private Integer queuePosition;

    private Boolean notificationSend;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean isExpired;
    private boolean canBeCancelled;
    private Long hoursUntilExpiry;
}
