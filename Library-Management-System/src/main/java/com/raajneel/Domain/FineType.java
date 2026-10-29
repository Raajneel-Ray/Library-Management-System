package com.raajneel.Domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Classification of the fine assessment",
        allowableValues = {"OVERDUE", "DAMAGE", "LOSS", "PROCESSING"}
)
public enum FineType {
    OVERDUE,
    DAMAGE,
    LOSS,
    PROCESSING
}
