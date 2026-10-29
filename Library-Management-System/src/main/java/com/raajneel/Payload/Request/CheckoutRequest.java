package com.raajneel.Payload.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request payload for checking out and borrowing a book")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutRequest {

    @NotNull(message = "Book ID is mandatory")
    @Schema(description = "Identifier of the book to checkout", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long bookId;

    @Min(value = 1, message = "Checkout days must be at least 1")
    @Schema(description = "Requested loan duration in days (defaults to user's subscription plan maximum)", example = "14", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer checkoutDays;

    @Schema(description = "Circulation desk notes or patron requests", example = "Checked out for university coursework research", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String notes;
}
