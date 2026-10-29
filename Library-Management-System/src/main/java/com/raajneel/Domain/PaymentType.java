package com.raajneel.Domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Type or reason for the payment",
        allowableValues = {"FINE", "MEMBERSHIP", "LOST_BOOK_PENALTY", "DAMAGED_BOOK_PENALTY", "REFUND"}
)
public enum PaymentType {
    FINE,
    MEMBERSHIP,
    LOST_BOOK_PENALTY,
    DAMAGED_BOOK_PENALTY,
    REFUND
}
