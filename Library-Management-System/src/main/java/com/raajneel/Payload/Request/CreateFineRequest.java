package com.raajneel.Payload.Request;

import com.raajneel.Domain.FineType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request payload for assessing and creating an administrative fine against a book loan")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateFineRequest {

    @NotNull(message = "Book Loan ID is mandatory")
    @Schema(description = "Identifier of the book loan incurring the fine", example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long bookLoanId;

    @NotNull(message = "Fine type is mandatory")
    @Schema(description = "Category of fine assessment", allowableValues = {"OVERDUE", "DAMAGE", "LOSS", "PROCESSING"}, example = "DAMAGE", requiredMode = Schema.RequiredMode.REQUIRED)
    private FineType type;

    @NotNull(message = "Fine amount is mandatory")
    @Positive(message = "Fine amount must be positive")
    @Schema(description = "Assessment fine amount in minor currency units (cents or paisa)", example = "2500", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long amount;

    @Schema(description = "Specific violation reason", example = "Water damage on back cover pages 400-450", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String reason;

    @Schema(description = "Staff internal remarks", example = "Assessed during return inspection", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String notes;
}
