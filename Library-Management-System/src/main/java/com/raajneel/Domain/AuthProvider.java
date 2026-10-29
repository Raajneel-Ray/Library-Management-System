package com.raajneel.Domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Authentication provider used for user registration and login",
        allowableValues = {"LOCAL", "GOOGLE", "FACEBOOK"}
)
public enum AuthProvider {
    LOCAL,
    GOOGLE,
    FACEBOOK
}
