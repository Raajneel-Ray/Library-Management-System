package com.raajneel.Payload.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVerifyRequest {
    private String razorpayPaymentId;

    private String stripePaymentIntentId;
    private String stripePaymentIntentStatus;
}
