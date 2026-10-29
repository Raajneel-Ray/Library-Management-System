package com.raajneel.Payload.Request;

import com.raajneel.Domain.PaymentGateway;
import com.raajneel.Domain.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request payload for initiating a payment checkout order with gateway")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInitiateRequest {

    @NotNull(message = "User ID is mandatory")
    @Schema(description = "Identifier of patron initiating payment", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "Associated book loan ID if settling loan penalty", example = "101", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long bookLoanId;

    @NotNull(message = "Payment type is mandatory")
    @Schema(description = "Category of transaction payment", allowableValues = {"FINE", "MEMBERSHIP", "LOST_BOOK_PENALTY", "DAMAGED_BOOK_PENALTY", "REFUND"}, example = "FINE", requiredMode = Schema.RequiredMode.REQUIRED)
    private PaymentType paymentType;

    @NotNull(message = "Payment gateway is mandatory")
    @Schema(description = "Payment processing gateway integration", allowableValues = {"RAZORPAY", "STRIPE"}, example = "RAZORPAY", requiredMode = Schema.RequiredMode.REQUIRED)
    private PaymentGateway gateway;

    @NotNull(message = "Amount is mandatory")
    @Positive(message = "Amount must be positive")
    @Schema(description = "Transaction charge in lowest currency denominator (paisa/cents)", example = "2500", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long amount;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Payment order description or memo", example = "Payment for overdue fine #201", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Identifier of the fine entity being paid", example = "201", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long fineId;

    @Schema(description = "Identifier of subscription membership being purchased", example = "12", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long subscriptionId;

    @Size(max = 500, message = "Success URL must not exceed 500 characters")
    @Schema(description = "Client redirect URL upon successful checkout completion", example = "https://library.example.com/payment/success", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String successUrl;

    @Size(max = 500, message = "Cancel URL must not exceed 500 characters")
    @Schema(description = "Client redirect URL if user cancels transaction", example = "https://library.example.com/payment/cancel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String cancelUrl;
}
