package com.raajneel.Domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Status of a book hold or reservation queue entry",
        allowableValues = {"PENDING", "AVAILABLE", "FULFILLED", "CANCELLED", "EXPIRED"}
)
public enum ReservationStatus {
    PENDING,
    AVAILABLE,
    FULFILLED,
    CANCELLED,
    EXPIRED
}
