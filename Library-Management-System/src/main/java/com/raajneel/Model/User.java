package com.raajneel.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raajneel.Domain.AuthProvider;
import com.raajneel.Domain.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entity model representing a user account in the system")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique user identifier", example = "5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    @Schema(description = "User primary email address", example = "alice.johnson@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "Bcrypt hashed password (write-only)", example = "$2a$10$7EqJtq98hPqEX7fNZaFWoO...", accessMode = Schema.AccessMode.WRITE_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Column(nullable = false, length = 100)
    @Schema(description = "Full legal name of the user", example = "Alice Johnson", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "Role assigned to the user", allowableValues = {"ROLE_USER", "ROLE_ADMIN"}, example = "ROLE_USER", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserRole role = UserRole.ROLE_USER;

    @Column(length = 20)
    @Schema(description = "Contact telephone number", example = "+1-555-019-2834", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Schema(description = "Identity provider used to authenticate", allowableValues = {"LOCAL", "GOOGLE", "FACEBOOK"}, example = "LOCAL", requiredMode = Schema.RequiredMode.REQUIRED)
    private AuthProvider authProvider = AuthProvider.LOCAL;

    @Schema(description = "Google OAuth subject identifier if linked", example = "109823471092384710293", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String googleId;

    @Column(length = 500)
    @Schema(description = "Profile avatar image URL", example = "https://images.example.com/avatars/alice.png", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String profileImage;

    @Schema(description = "Timestamp of the user's last login session", example = "2026-10-28T18:45:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime lastLogin;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Schema(description = "Account registration timestamp", example = "2026-01-10T12:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    @Schema(description = "Account last updated timestamp", example = "2026-10-28T18:45:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime updatedAt;
}
