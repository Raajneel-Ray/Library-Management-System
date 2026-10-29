package com.raajneel.Domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Type of loan action performed",
        allowableValues = {"CHECKOUT", "RENEWAL", "RETURN"}
)
public enum BookLoanType {
    CHECKOUT,
    RENEWAL,
    RETURN,
}
