package com.raajneel.Payload.Response;

import com.raajneel.Payload.DTO.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Response returned upon successful authentication containing JWT and user profile")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    @Schema(description = "Signed JSON Web Token (JWT) for Bearer authentication", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String jwt;

    @Schema(description = "Descriptive confirmation message", example = "Login successful", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    @Schema(description = "Short notification title", example = "Authentication Successful", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String title;

    @Schema(description = "Authenticated user profile details", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserDTO user;
}
