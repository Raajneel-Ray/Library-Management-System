package com.raajneel.Domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Status of a book loan transaction",
        allowableValues = {"CHECKED_OUT", "RETURNED", "OVERDUE", "LOST", "DAMAGED"}
)
public enum BookLoanStatus {
    CHECKED_OUT,
    RETURNED,
    OVERDUE,
    LOST,
    DAMAGED
}
