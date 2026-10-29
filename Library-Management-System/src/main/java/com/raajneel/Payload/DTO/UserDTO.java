package com.raajneel.Payload.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raajneel.Domain.AuthProvider;
import com.raajneel.Domain.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "Data transfer object representing a library user profile or signup request")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @Schema(description = "Unique user identifier", example = "5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    @Schema(description = "User primary email address used for login and notifications", example = "alice.johnson@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "Raw password for authentication (write-only, never returned in responses)", example = "SecurePass123!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotBlank(message = "Full name is required")
    @Schema(description = "Full legal name of the user", example = "Alice Johnson", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fullName;

    @Schema(description = "System authorization role of the user", allowableValues = {"ROLE_USER", "ROLE_ADMIN"}, example = "ROLE_USER", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UserRole role;

    @Schema(description = "Contact phone number with international dial code", example = "+1-555-019-2834", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String phone;

    @Schema(description = "User account username or handle", example = "alice_j", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String userName;

    @Schema(description = "Timestamp of most recent successful user login session", example = "2026-10-28T18:45:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime lastLogin;
}
