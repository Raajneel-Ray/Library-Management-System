package com.raajneel.Domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Status of a payment transaction",
        allowableValues = {"PENDING", "PROCESSING", "SUCCESS", "FAILED", "CANCELLED"}
)
public enum PaymentStatus {
    PENDING,
    PROCESSING,
    SUCCESS,
    FAILED,
    CANCELLED
}
