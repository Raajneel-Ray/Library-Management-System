package com.raajneel.Payload.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Data transfer object representing a book record in the catalog")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookDTO {

    @Schema(description = "Unique database identifier of the book", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @NotBlank(message = "ISBN is mandatory")
    @Schema(description = "13-digit International Standard Book Number (ISBN)", example = "978-0132350884", requiredMode = Schema.RequiredMode.REQUIRED)
    private String isbn;

    @NotBlank(message = "Title is mandatory")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    @Schema(description = "Full title of the book", example = "Clean Code: A Handbook of Agile Software Craftsmanship", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank(message = "Author is mandatory")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    @Schema(description = "Primary author or creator of the book", example = "Robert C. Martin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String author;

    @NotNull(message = "Genre is mandatory")
    @Schema(description = "Unique identifier of the genre category", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long genreId;

    @Schema(description = "Display name of the associated genre", example = "Software Engineering", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String genreName;

    @Schema(description = "Unique alphanumeric code of the genre", example = "TECH-SE", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String genreCode;

    @Size(max = 100, message = "Publisher name must not exceed 100 characters")
    @Schema(description = "Publishing house or company", example = "Prentice Hall", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String publisher;

    @Schema(description = "Official publication date", example = "2008-08-01", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDate publicationDate;

    @Size(max = 20, message = "Language must not exceed 20 characters")
    @Schema(description = "Language in which the book is written", example = "English", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String language;

    @Min(value = 1, message = "Pages must be at least 1")
    @Max(value = 50000, message = "Pages must not exceed 50000")
    @Schema(description = "Total number of pages", example = "464", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer pages;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Schema(description = "Synopsis or detailed description of the book", example = "Even bad code can function. But if code isn't clean, it can bring a development organization to its knees.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Min(value = 0, message = "Total copies cannot be negative")
    @NotNull(message = "Total copies is mandatory")
    @Schema(description = "Total physical copies acquired by the library", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalCopies;

    @Min(value = 0, message = "Available copies cannot be negative")
    @NotNull(message = "Available copies is mandatory")
    @Schema(description = "Current number of copies in stock available for checkout", example = "7", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer availableCopies;

    @DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 integer and 2 decimal places")
    @Schema(description = "Retail or replacement price of the book", example = "39.99", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BigDecimal price;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    @Schema(description = "Direct HTTPS URL to the cover art image", example = "https://images.example.com/books/cleancode.jpg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String coverImageUrl;

    @Schema(description = "Indicates whether the authenticated user currently has this book on loan", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean alreadyHaveLoan;

    @Schema(description = "Indicates whether the authenticated user currently holds an active reservation for this book", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean alreadyHaveReservation;

    @Schema(description = "Soft delete / active listing status", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean active;

    @Schema(description = "Timestamp when the book record was initially inserted", example = "2026-01-15T10:30:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the book record was last updated", example = "2026-03-20T14:45:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime updatedAt;
}
