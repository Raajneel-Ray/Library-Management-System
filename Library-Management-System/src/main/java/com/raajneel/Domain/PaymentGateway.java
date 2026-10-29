package com.raajneel.Domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Supported online payment gateway providers",
        allowableValues = {"RAZORPAY", "STRIPE"}
)
public enum PaymentGateway {
    RAZORPAY,
    STRIPE
}
