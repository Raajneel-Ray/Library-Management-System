package com.raajneel.Payload.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Payload model for creating or modifying a book review")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookReviewRequest {

    @NotNull(message = "Book ID is required")
    @Schema(description = "Identifier of the book being reviewed", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long bookId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    @Schema(description = "Star rating score from 1 (lowest) to 5 (highest)", example = "5", minimum = "1", maximum = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer rating;

    @Size(max = 2000, message = "Review text cannot exceed 2000 characters")
    @Schema(description = "Personal review comments and critique (up to 2000 characters)", example = "Extremely well written with practical patterns for writing clean, maintainable software.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String reviewText;
}
