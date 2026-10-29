package com.raajneel.Payload.Request;

import com.raajneel.Domain.BookLoanStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request payload for checking in and returning a borrowed book")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckinRequest {

    @NotNull(message = "Book loan ID is mandatory")
    @Schema(description = "Identifier of the book loan to check in", example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long bookLoanId;

    @Schema(description = "Condition and return status of the physical copy", allowableValues = {"RETURNED", "LOST", "DAMAGED"}, example = "RETURNED", defaultValue = "RETURNED", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BookLoanStatus condition = BookLoanStatus.RETURNED;

    @Schema(description = "Circulation inspection notes upon book return", example = "Returned on time, cover clean and bindings intact", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String notes;
}
