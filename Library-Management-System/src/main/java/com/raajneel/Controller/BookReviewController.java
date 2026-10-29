package com.raajneel.Controller;

import com.raajneel.Payload.DTO.BookReviewDTO;
import com.raajneel.Payload.DTO.BookReviewSummaryDTO;
import com.raajneel.Payload.Request.BookReviewRequest;
import com.raajneel.Payload.Response.ApiResponse;
import com.raajneel.Payload.Response.PageResponse;
import com.raajneel.Service.BookReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/review", "/api/reviews"})
public class BookReviewController {

    private final BookReviewService bookReviewService;

    @PostMapping
    public ResponseEntity<BookReviewDTO> createReview(@Valid @RequestBody BookReviewRequest request) throws Exception {
        BookReviewDTO review = bookReviewService.createReview(request);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookReviewDTO> updateReview(
            @PathVariable Long id,
            @RequestBody BookReviewRequest request
    ) throws Exception {
        BookReviewDTO review = bookReviewService.updateReview(id, request);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteReview(@PathVariable Long id) throws Exception {
        bookReviewService.deleteReview(id);
        return ResponseEntity.ok(new ApiResponse("Review deleted successfully", true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookReviewDTO> getReviewById(@PathVariable Long id) throws Exception {
        BookReviewDTO review = bookReviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<PageResponse<BookReviewDTO>> getReviewsByBookId(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        PageResponse<BookReviewDTO> response = bookReviewService.getReviewsByBookId(bookId, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/book/{bookId}/summary")
    public ResponseEntity<BookReviewSummaryDTO> getBookReviewSummary(@PathVariable Long bookId) {
        BookReviewSummaryDTO summary = bookReviewService.getBookReviewSummary(bookId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/my")
    public ResponseEntity<PageResponse<BookReviewDTO>> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) throws Exception {
        PageResponse<BookReviewDTO> response = bookReviewService.getMyReviews(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PageResponse<BookReviewDTO>> getReviewsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        PageResponse<BookReviewDTO> response = bookReviewService.getReviewsByUserId(userId, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }
}
