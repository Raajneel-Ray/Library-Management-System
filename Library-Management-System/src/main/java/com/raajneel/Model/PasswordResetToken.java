package com.raajneel.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens") // Explicit table name adheres to standard plural naming conventions
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Native MySQL auto-increment instead of table-based sequence emulation
    private Long id;

    @Column(nullable = false, unique = true, length = 100) // Explicit token length limit suitable for UUID or generated tokens
    private String token;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY fetch avoids loading User eagerly when querying tokens
    @JoinColumn(name = "user_id", nullable = false) // Explicit foreign key column name
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false) // updatable = false prevents modification of creation timestamp; useful for audit/cleanup
    private LocalDateTime createdAt;

    public boolean isExpired() {
        // Guard against null expiryDate to prevent NullPointerException
        return expiryDate != null && expiryDate.isBefore(LocalDateTime.now());
    }
}
