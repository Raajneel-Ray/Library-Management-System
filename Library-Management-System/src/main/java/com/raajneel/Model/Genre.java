package com.raajneel.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.raajneel.LibraryManagementSystemApplication;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Genre code is Mandatory")
    private String code;

    @NotBlank(message = "Genre name is Mandatory")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Min(value = 0, message = "Display order cannot be negative")
    private Integer displayOrder = 0;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne
    private Genre parentGenre;

    @OneToMany(mappedBy = "parentGenre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Genre> subGenres = new ArrayList<Genre>();

    @OneToMany(mappedBy = "genre", cascade = CascadeType.PERSIST)
    @JsonManagedReference("genre-books")
    private List<Book> books = new ArrayList<Book>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
