package com.raajneel.Mapper;

import com.raajneel.Model.BookReview;
import com.raajneel.Payload.DTO.BookReviewDTO;
import org.springframework.stereotype.Component;

@Component
public class BookReviewMapper {

    public BookReviewDTO toDTO(BookReview review) {
        if (review == null) {
            return null;
        }

        BookReviewDTO dto = new BookReviewDTO();
        dto.setId(review.getId());

        if (review.getBook() != null) {
            dto.setBookId(review.getBook().getId());
            dto.setBookTitle(review.getBook().getTitle());
            dto.setBookAuthor(review.getBook().getAuthor());
            dto.setBookCoverImageUrl(review.getBook().getCoverImageUrl());
        }

        if (review.getUser() != null) {
            dto.setUserId(review.getUser().getId());
            dto.setUserName(review.getUser().getFullName());
            dto.setUserProfileImage(review.getUser().getProfileImage());
        }

        dto.setRating(review.getRating());
        dto.setReviewText(review.getReviewText());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());

        return dto;
    }
}
