package com.raajneel.Payload.DTO;

import com.raajneel.Domain.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "Data transfer object representing a book reservation or queue hold request")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationDTO {

    @Schema(description = "Unique identifier of the reservation record", example = "401", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @Schema(description = "Identifier of the reserving patron", example = "5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long userId;

    @Schema(description = "Name of the reserving patron", example = "Alice Johnson", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String userName;

    @Schema(description = "Email of the reserving patron", example = "alice.johnson@example.com", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String userEmail;

    @Schema(description = "Identifier of the reserved book", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long bookId;

    @Schema(description = "Title of the reserved book", example = "Clean Code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bookTitle;

    @Schema(description = "ISBN of the reserved book", example = "978-0132350884", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bookIsbn;

    @Schema(description = "Author of the reserved book", example = "Robert C. Martin", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bookAuthor;

    @Schema(description = "Indicates whether a physical copy is currently available on shelf", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean isBookAvailable;

    @Schema(description = "Current lifecycle status of reservation", allowableValues = {"PENDING", "AVAILABLE", "FULFILLED", "CANCELLED", "EXPIRED"}, example = "PENDING", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ReservationStatus status;

    @Schema(description = "Timestamp when reservation was placed", example = "2026-10-20T10:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime reservedAt;

    @Schema(description = "Timestamp when a returned book became available for this reservation", example = "2026-10-24T12:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime availableAt;

    @Schema(description = "Hold expiration deadline before book is passed to next in queue", example = "2026-10-27T23:59:59", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime availableUntil;

    @Schema(description = "Timestamp when patron checked out the reserved book", example = "2026-10-25T14:30:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime fulfilledAt;

    @Schema(description = "Timestamp when reservation was cancelled", example = "2026-10-22T09:15:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime cancelledAt;

    @Schema(description = "Current position in waiting list queue (1 = next to receive available copy)", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer queuePosition;

    @Schema(description = "Flag indicating whether pickup notification email/SMS was dispatched", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean notificationSend;

    @Schema(description = "Patron notes or special handling instructions", example = "Please hold at main circulation desk", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String notes;

    @Schema(description = "Record creation timestamp", example = "2026-10-20T10:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime createdAt;

    @Schema(description = "Record last updated timestamp", example = "2026-10-24T12:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime updatedAt;

    @Schema(description = "Flag indicating if the hold pickup window has expired", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private boolean isExpired;

    @Schema(description = "Flag indicating if reservation is eligible to be cancelled by patron", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private boolean canBeCancelled;

    @Schema(description = "Number of whole hours remaining before pickup hold expires", example = "48", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long hoursUntilExpiry;
}
