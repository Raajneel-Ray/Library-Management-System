package com.raajneel.Payload.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request payload for renewing and extending due date of an active book loan")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenewRequest {

    @NotNull(message = "Book loan ID is mandatory")
    @Schema(description = "Identifier of active book loan being renewed", example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long bookLoanId;

    @Min(value = 1, message = "Extension days must be at least 1")
    @Schema(description = "Number of additional days requested for renewal extension", example = "14", defaultValue = "14", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer extensionDay = 14;

    @Schema(description = "Patron renewal remarks or notes", example = "Need extra week to finish dissertation chapter", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String notes;
}
