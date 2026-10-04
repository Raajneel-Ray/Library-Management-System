package com.raajneel.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "genres") // Explicit table name avoids database keyword conflicts and adheres to standard naming conventions
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Native MySQL auto-increment instead of table-based sequence emulation
    private Long id;

    @NotBlank(message = "Genre code is Mandatory")
    @Column(nullable = false, unique = true, length = 50) // DB-level uniqueness and size constraint for genre code
    private String code;

    @NotBlank(message = "Genre name is Mandatory")
    @Column(nullable = false, length = 100) // DB-level non-null and size constraint for genre name
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(length = 500) // Matches @Size(max = 500) to prevent default VARCHAR(255) truncation
    private String description;

    @Min(value = 0, message = "Display order cannot be negative")
    @Builder.Default // Retains default value when instantiated via Lombok @Builder
    private Integer displayOrder = 0;

    @Builder.Default // Retains default value when instantiated via Lombok @Builder
    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY fetch prevents eager N+1 query performance overhead
    @JoinColumn(name = "parent_genre_id") // Explicit foreign key column name
    private Genre parentGenre;

    @Builder.Default // Prevents null list on builder instantiation, avoiding NullPointerExceptions
    @OneToMany(mappedBy = "parentGenre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Genre> subGenres = new ArrayList<Genre>();

    @Builder.Default // Prevents null list on builder instantiation, avoiding NullPointerExceptions
    @OneToMany(mappedBy = "genre", cascade = CascadeType.PERSIST)
    @JsonManagedReference("genre-books")
    private List<Book> books = new ArrayList<Book>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false) // updatable = false prevents modification of creation timestamp on entity updates
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Synchronization helper method to maintain bidirectional relationship in memory
    public void addSubGenre(Genre subGenre) {
        this.subGenres.add(subGenre);
        subGenre.setParentGenre(this);
    }

    // Synchronization helper method to maintain bidirectional relationship in memory
    public void removeSubGenre(Genre subGenre) {
        this.subGenres.remove(subGenre);
        subGenre.setParentGenre(null);
    }
}
