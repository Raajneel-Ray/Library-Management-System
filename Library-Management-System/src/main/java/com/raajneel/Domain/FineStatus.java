package com.raajneel.Domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Status of a fine balance",
        allowableValues = {"PENDING", "PARTIALLY_PAID", "PAID", "WAIVED"}
)
public enum FineStatus {
    PENDING,
    PARTIALLY_PAID,
    PAID,
    WAIVED
}
