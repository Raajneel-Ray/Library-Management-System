package com.raajneel.Service;

import com.raajneel.Payload.DTO.BookReviewDTO;
import com.raajneel.Payload.DTO.BookReviewSummaryDTO;
import com.raajneel.Payload.Request.BookReviewRequest;
import com.raajneel.Payload.Response.PageResponse;

public interface BookReviewService {

    BookReviewDTO createReview(BookReviewRequest request) throws Exception;

    BookReviewDTO updateReview(Long reviewId, BookReviewRequest request) throws Exception;

    void deleteReview(Long reviewId) throws Exception;

    BookReviewDTO getReviewById(Long reviewId) throws Exception;

    PageResponse<BookReviewDTO> getReviewsByBookId(Long bookId, int page, int size, String sortBy, String sortDirection);

    PageResponse<BookReviewDTO> getMyReviews(int page, int size, String sortBy, String sortDirection) throws Exception;

    PageResponse<BookReviewDTO> getReviewsByUserId(Long userId, int page, int size, String sortBy, String sortDirection);

    BookReviewSummaryDTO getBookReviewSummary(Long bookId);
}
