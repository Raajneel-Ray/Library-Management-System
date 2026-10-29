package com.raajneel.Payload.Response;

import com.raajneel.Domain.PaymentGateway;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Response returned upon initializing checkout with payment gateway")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInitiateResponse {

    @Schema(description = "Identifier of the paying patron", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "System payment transaction record ID", example = "301", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long paymentId;

    @Schema(description = "Payment gateway provider processing order", allowableValues = {"RAZORPAY", "STRIPE"}, example = "RAZORPAY", requiredMode = Schema.RequiredMode.REQUIRED)
    private PaymentGateway gateway;

    @Schema(description = "System internal transaction reference number", example = "TXN_9876543210", requiredMode = Schema.RequiredMode.REQUIRED)
    private String transactionId;

    @Schema(description = "Gateway order identifier (e.g., Razorpay order ID)", example = "order_DBJOWzybf0sJbb", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String razorpayOrderId;

    @Schema(description = "Total payable charge amount in smallest currency unit (cents or paisa)", example = "2500", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long amount;

    @Schema(description = "Payment statement summary descriptor", example = "Fine payment for loan #101", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Hosted payment checkout URL for patron redirect", example = "https://api.razorpay.com/v1/checkout/public?order_id=order_DBJOWzybf0sJbb", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String checkOutUrl;

    @Schema(description = "Informative status message", example = "Payment order initiated successfully", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String message;

    @Schema(description = "Flag indicating if order initiation succeeded", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean success;
}
