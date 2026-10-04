package com.raajneel.Model;
import com.raajneel.Domain.AuthProvider;
import com.raajneel.Domain.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users") // Explicit table name avoids database reserved keyword 'user' conflicts
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Native MySQL auto-increment instead of table-based sequence emulation
    private Long id;

    @Column(nullable = false, unique = true, length = 150) // DB-level uniqueness and size constraint for email
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100) // Explicit column size constraint for user full name
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserRole role = UserRole.ROLE_USER;

    @Column(length = 20) // Phone numbers typically 10-15 chars
    private String phone;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AuthProvider authProvider = AuthProvider.LOCAL;

    private String googleId;

    @Column(length = 500) // Profile image URLs can exceed default 255 chars
    private String profileImage;

    private LocalDateTime lastLogin;

    @CreationTimestamp
    @Column(nullable = false, updatable = false) // updatable = false prevents modification of creation timestamp on entity updates
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

