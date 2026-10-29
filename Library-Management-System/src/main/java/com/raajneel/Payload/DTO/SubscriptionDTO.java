package com.raajneel.Payload.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Data transfer object representing a patron's library membership subscription")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDTO {

    @Schema(description = "Unique identifier of the subscription instance", example = "12", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @NotNull(message = "User Id is mandatory")
    @Schema(description = "Identifier of subscribed user", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "Full name of subscriber", example = "Alice Johnson", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String userName;

    @Schema(description = "Email of subscriber", example = "alice.johnson@example.com", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String userEmail;

    @NotNull(message = "Subscription Plan ID is mandatory")
    @Schema(description = "Identifier of the associated subscription tier plan", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long planId;

    @Schema(description = "Display name of subscription plan", example = "Premium Scholar", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String planName;

    @Schema(description = "Unique code of subscription plan", example = "PREM-SCHOLAR", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String planCode;

    @Schema(description = "Cost of plan in minor currency units (cents/paisa)", example = "4999", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long price;

    @Schema(description = "ISO-4217 Currency code", example = "INR", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String currency;

    @Schema(description = "Formatted price display string", example = "49.99", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String priceInMajorUnits;

    @Schema(description = "Effective start date of membership", example = "2026-10-01", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDate startDate;

    @Schema(description = "Membership expiration date", example = "2026-11-01", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDate endDate;

    @Schema(description = "Whether membership is currently active and in good standing", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean isActive;

    @Schema(description = "Maximum concurrent book loans permitted under this plan", example = "5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer maxBooksAllowed;

    @Schema(description = "Standard checkout duration in days per borrowed book", example = "21", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer maxDaysPerBook;

    @Schema(description = "Whether the subscription renews automatically upon billing cycle end", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean autoRenew;

    @Schema(description = "Timestamp when cancellation was requested if cancelled prematurely", example = "2026-10-25T10:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime cancelledDate;

    @Schema(description = "Reason entered for membership cancellation", example = "Relocating to another city", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String cancellationReason;

    @Schema(description = "Staff or system remarks regarding the subscription", example = "Promotional student discount applied", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String notes;

    @Schema(description = "Calendar days remaining before subscription expiry", example = "28", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long daysRemaining;

    @Schema(description = "Whether the subscription is valid for checkout operations today", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean isValid;

    @Schema(description = "Whether the subscription has passed its end date", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean isExpired;

    @Schema(description = "Record creation timestamp", example = "2026-10-01T00:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime createdAt;

    @Schema(description = "Record last updated timestamp", example = "2026-10-01T00:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime updatedAt;
}
