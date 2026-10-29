package com.raajneel.Payload.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request payload for finalizing password change using recovery token")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "Token is required")
    @Schema(description = "Cryptographic password reset token sent via recovery email", example = "a8b9c0d1-e2f3-4a5b-6c7d-8e9f0a1b2c3d", requiredMode = Schema.RequiredMode.REQUIRED)
    private String token;

    @NotBlank(message = "New password field is required")
    @Size(min = 6, message = "New password must be at least 6 characters")
    @Schema(description = "New password to set for user account", example = "BrandNewSecurePass456!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}
