package com.raajneel.Payload.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "Data transfer object representing a library membership subscription plan tier")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionPlanDTO {

    @Schema(description = "Unique identifier of the subscription plan", example = "2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @NotBlank(message = "Plan code is mandatory")
    @Schema(description = "Unique system code for the subscription plan", example = "PREM-SCHOLAR", requiredMode = Schema.RequiredMode.REQUIRED)
    private String planCode;

    @NotBlank(message = "Plan name is mandatory")
    @Schema(description = "Display name of the subscription plan", example = "Premium Scholar Plan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String planName;

    @Schema(description = "Marketing and feature overview of what this tier includes", example = "Ideal for university researchers needing up to 5 concurrent loans for 21 days each.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @NotNull(message = "Duration days of plan is mandatory")
    @Positive(message = "Duration days must be positive")
    @Schema(description = "Active validity period in days", example = "30", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer durationDays;

    @NotNull(message = "Price is mandatory")
    @Positive(message = "Price value must be positive")
    @Schema(description = "Price in minor currency units (cents or paisa)", example = "4999", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long price;

    @Schema(description = "ISO-4217 Currency symbol or code", example = "INR", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String currency;

    @NotNull(message = "Max books allowed is mandatory")
    @Positive(message = "Max Books to be allowed must be positive")
    @Schema(description = "Maximum concurrent active book loans permitted", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer maximumBooksAllowed;

    @NotNull(message = "Max Days per book is mandatory")
    @Positive(message = "Max Days must be positive")
    @Schema(description = "Standard checkout loan duration in days per book", example = "21", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer maxDaysPerBook;

    @Schema(description = "Sorting/display position on plans pricing page", example = "2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer displayOrder;

    @Schema(description = "Whether the plan is actively available for purchase", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean isActive;

    @Schema(description = "Whether to highlight this tier as a featured/recommended choice", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean isFeatured;

    @Schema(description = "Ribbon badge text on plan card (e.g., 'Most Popular')", example = "Most Popular", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String badgeText;

    @Schema(description = "Internal administrator notes", example = "Targeted for student promotions during semester opening", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String adminNotes;

    @Schema(description = "Record creation timestamp", example = "2026-01-01T00:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime createdAt;

    @Schema(description = "Record last updated timestamp", example = "2026-03-01T12:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime updatedAt;

    @Schema(description = "Admin username who created this plan record", example = "admin_super", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String createdBy;

    @Schema(description = "Admin username who last modified this plan record", example = "admin_super", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String updatedBy;
}
