package com.raajneel.Payload.Request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    @NotNull(message = "Token cannot be null")
    private String token;
    @NotNull(message = "New password field cannot be null")
    private String newPassword;
}
