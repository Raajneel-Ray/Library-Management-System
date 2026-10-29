package com.raajneel.Payload.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "Data transfer object representing a book saved in a user's reading wishlist")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishListDTO {

    @Schema(description = "Unique identifier of the wishlist record", example = "601", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @Schema(description = "Identifier of the owner user", example = "5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long userId;

    @Schema(description = "Display name of the owner user", example = "Alice Johnson", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String userName;

    @Schema(description = "Identifier of the book saved to wishlist", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long bookId;

    @Schema(description = "Title of the saved book", example = "Clean Code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bookTitle;

    @Schema(description = "Author of the saved book", example = "Robert C. Martin", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bookAuthor;

    @Schema(description = "ISBN number of the saved book", example = "978-0132350884", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bookIsbn;

    @Schema(description = "Direct URL to the cover art image", example = "https://images.example.com/books/cleancode.jpg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bookCoverImageUrl;

    @Schema(description = "Current count of shelf copies available in library inventory", example = "7", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer availableCopies;

    @Schema(description = "Whether at least one copy is immediately available for borrowing", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean isBookAvailable;

    @Schema(description = "Personal reading plan note or reminder", example = "Recommended by team lead for reading next quarter", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String notes;

    @Schema(description = "Timestamp when added to wishlist", example = "2026-10-22T08:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when wishlist entry was updated", example = "2026-10-22T08:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime updatedAt;
}
