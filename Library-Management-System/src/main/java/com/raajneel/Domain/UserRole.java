package com.raajneel.Domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "System authorization role of the user",
        allowableValues = {"ROLE_USER", "ROLE_ADMIN"}
)
public enum UserRole {
    ROLE_USER,
    ROLE_ADMIN
}
