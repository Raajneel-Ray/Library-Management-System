package com.raajneel.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "books") // Explicit table name avoids database keyword conflicts and adheres to standard naming conventions
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Native MySQL auto-increment instead of table-based sequence emulation
    private Long id;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY fetch prevents eager N+1 query performance overhead when querying books
    @JoinColumn(name = "genre_id", nullable = false) // Explicit foreign key column name
    @JsonBackReference("genre-books")
    private Genre genre;

    @Column(length = 100) // Explicit column size constraint matching DTO limits
    private String publisher;

    private LocalDate publishedDate;

    @Column(length = 20) // Language code or name size constraint
    private String language;

    private Integer pages;

    @Column(length = 2000) // Expanded from default VARCHAR(255) to support up to 2000 characters as permitted by BookDTO
    private String description;

    @Column(nullable = false)
    private Integer totalCopies;

    @Column(nullable = false)
    private Integer availableCopies;

    private BigDecimal price;

    @Column(length = 500) // URLs often exceed default 255 chars; matches BookDTO max limit of 500
    private String coverImageUrl;

    @Builder.Default // Ensures default value true is retained when instantiated via Lombok @Builder
    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false) // updatable = false prevents modification of creation timestamp on entity updates
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @AssertTrue(message = "Available copies must not exceed total copies and cannot be negative")
    public boolean isAvailableCopiesValid() {
        if(totalCopies == null || availableCopies == null) {
            return true;
        }
        // Verify non-negative copies and available copies do not exceed total copies
        return availableCopies >= 0 && totalCopies >= 0 && availableCopies <= totalCopies;
    }
}
