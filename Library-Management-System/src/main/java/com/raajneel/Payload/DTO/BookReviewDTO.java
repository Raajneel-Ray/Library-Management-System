package com.raajneel.Payload.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "Data transfer object representing a book review submitted by a patron")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookReviewDTO {

    @Schema(description = "Unique identifier of the review", example = "501", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @Schema(description = "Identifier of the reviewed book", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long bookId;

    @Schema(description = "Title of the reviewed book", example = "Clean Code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bookTitle;

    @Schema(description = "Author of the reviewed book", example = "Robert C. Martin", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bookAuthor;

    @Schema(description = "Cover art URL of the reviewed book", example = "https://images.example.com/books/cleancode.jpg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bookCoverImageUrl;

    @Schema(description = "Identifier of the reviewing user", example = "5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long userId;

    @Schema(description = "Display name of the reviewer", example = "Alice Johnson", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String userName;

    @Schema(description = "Profile avatar URL of the reviewer", example = "https://images.example.com/avatars/alice.png", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String userProfileImage;

    @Schema(description = "Numerical rating on a scale of 1 to 5 stars", example = "5", minimum = "1", maximum = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer rating;

    @Schema(description = "Detailed written feedback or thoughts regarding the book", example = "A timeless classic that transformed the way I structure methods and classes.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String reviewText;

    @Schema(description = "Timestamp when the review was originally posted", example = "2026-10-18T16:20:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the review was last edited", example = "2026-10-19T08:30:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime updatedAt;
}
