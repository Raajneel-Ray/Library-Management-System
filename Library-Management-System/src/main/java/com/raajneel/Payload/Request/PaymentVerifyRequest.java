package com.raajneel.Payload.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request payload for verifying and finalizing gateway payment signatures")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVerifyRequest {

    @Schema(description = "Payment ID issued by Razorpay upon successful checkout", example = "pay_N83kS0v81Lkdfg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String razorpayPaymentId;

    @Schema(description = "Payment intent ID returned by Stripe checkout", example = "pi_3MtwBwLkdIwHu7ix28a3tqPa", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String stripePaymentIntentId;

    @Schema(description = "Payment intent status reported by Stripe SDK or webhook", example = "succeeded", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String stripePaymentIntentStatus;
}
