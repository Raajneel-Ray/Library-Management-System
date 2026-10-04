package com.raajneel.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_plans") // Explicit table name adheres to standard plural naming conventions
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Native MySQL auto-increment instead of table-based sequence emulation
    private Long id;

    @Column(nullable = false, unique = true, length = 50) // DB-level uniqueness and size constraint for plan code
    private String planCode;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500) // Expanded from default VARCHAR(255) to prevent truncation of plan descriptions
    private String description;

    @Column(nullable = false)
    private Integer durationDays;

    @Column(nullable = false)
    private Long price;

    @Builder.Default // Retains default currency INR when instantiated via Lombok @Builder
    @Column(length = 10)
    private String currency = "INR";

    @Column(nullable = false)
    @Positive(message = "Max Books must be positive")
    private Integer maximumBooksAllowed;

    @Column(nullable = false)
    @Positive(message = "Max days per books must be positive")
    private Integer maxDaysPerBook;

    @Builder.Default // Retains default value 0 when instantiated via Lombok @Builder
    private Integer displayOrder = 0;

    @Builder.Default // Retains default active state (true) when instantiated via Lombok @Builder
    private Boolean isActive = true;

    @Builder.Default // Retains default false when instantiated via Lombok @Builder
    private Boolean isFeatured = false;

    @Column(length = 50)
    private String badgeText;

    @Column(length = 500) // Expanded to prevent truncation of admin notes
    private String adminNotes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false) // updatable = false prevents modification of creation timestamp on entity updates
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedDate;

    private String createdBy;
    private String updatedBy;
}
