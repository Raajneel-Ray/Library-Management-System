package com.raajneel.Payload.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Statistical summary of ratings and review counts for a book")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookReviewSummaryDTO {

    @Schema(description = "Identifier of the target book", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long bookId;

    @Schema(description = "Calculated average rating score (out of 5.0)", example = "4.7", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double averageRating;

    @Schema(description = "Total count of approved reviews submitted for this book", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long totalReviews;
}
