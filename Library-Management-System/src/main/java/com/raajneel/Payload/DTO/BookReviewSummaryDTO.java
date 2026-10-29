package com.raajneel.Payload.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookReviewSummaryDTO {
    private Long bookId;
    private Double averageRating;
    private Long totalReviews;
}
