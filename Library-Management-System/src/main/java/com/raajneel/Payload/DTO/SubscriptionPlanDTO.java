package com.raajneel.Payload.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionPlanDTO {

    private Long id;

    @NotBlank(message = "Plan code is mandatory")
    private String planCode;

    @NotBlank(message = "Plan name is mandatory")
    private String planName;

    private String description;

    @NotNull(message = "Duration days of plan is mandatory")
    @Positive(message = "Duration days must be positive")
    private Integer durationDays;

    @NotNull(message = "Price is mandatory")
    @Positive(message = "Price value must be positive")

    private Long price;
    private String currency;

    @NotNull(message = "Max books allowed is mandatory")
    @Positive(message = "Max Books to be allowed must be positive")
    private Integer maximumBooksAllowed;

    @NotNull(message = "Max Days per book is mandatory")
    @Positive(message = "Max Days must be positive")
    private Integer maxDaysPerBook;

    private Integer displayOrder;
    private Boolean isActive;
    private Boolean isFeatured;
    private String badgeText;
    private String adminNotes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

}
