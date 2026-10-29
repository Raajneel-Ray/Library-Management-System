package com.raajneel.Controller;

import com.raajneel.Payload.DTO.BookReviewDTO;
import com.raajneel.Payload.DTO.BookReviewSummaryDTO;
import com.raajneel.Payload.Request.BookReviewRequest;
import com.raajneel.Payload.Response.ApiResponse;
import com.raajneel.Payload.Response.PageResponse;
import com.raajneel.Service.BookReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/review", "/api/reviews"})
@Tag(name = "Book Reviews", description = "Endpoints for patrons to submit, edit, read, and delete book ratings and written reviews")
@SecurityRequirement(name = "bearerAuth")
public class BookReviewController {

    private final BookReviewService bookReviewService;

    @PostMapping
    @Operation(
            summary = "Submit a book review",
            description = "Submits a rating (1-5) and written review text for a book. Each user can review a book at most once."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Review submitted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookReviewDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "User has already submitted a review for this book, or book not found, or validation failed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access - JWT token required",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server processing failure",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<BookReviewDTO> createReview(
            @Valid @RequestBody BookReviewRequest request) throws Exception {
        BookReviewDTO review = bookReviewService.createReview(request);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing book review",
            description = "Updates the rating score or comment text of a previously submitted review. Only the original author or an administrator may update."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Review updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookReviewDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Review ID not found or unauthorized modification attempt",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<BookReviewDTO> updateReview(
            @Parameter(description = "Identifier of review to modify", example = "501", required = true)
            @PathVariable Long id,
            @RequestBody BookReviewRequest request
    ) throws Exception {
        BookReviewDTO review = bookReviewService.updateReview(id, request);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a book review",
            description = "Removes a review from the database. Allowed for the review author or administrators."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Review successfully deleted",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Review not found or permission denied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse> deleteReview(
            @Parameter(description = "Identifier of review to delete", example = "501", required = true)
            @PathVariable Long id) throws Exception {
        bookReviewService.deleteReview(id);
        return ResponseEntity.ok(new ApiResponse("Review deleted successfully", true));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get review by ID",
            description = "Retrieves a single review by its primary identifier."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Review retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookReviewDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Review not found for given ID",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<BookReviewDTO> getReviewById(
            @Parameter(description = "Review record identifier", example = "501", required = true)
            @PathVariable Long id) throws Exception {
        BookReviewDTO review = bookReviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/book/{bookId}")
    @Operation(
            summary = "Get paginated reviews for a book",
            description = "Returns a paginated list of reviews submitted for a specific book title with sort options."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Paginated reviews list returned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<PageResponse<BookReviewDTO>> getReviewsByBookId(
            @Parameter(description = "Book identifier", example = "1", required = true)
            @PathVariable Long bookId,
            @Parameter(description = "Page index (0-based)", example = "0", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10", required = false)
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort property", example = "createdAt", required = false)
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)", schema = @Schema(allowableValues = {"ASC", "DESC"}), example = "DESC", required = false)
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        PageResponse<BookReviewDTO> response = bookReviewService.getReviewsByBookId(bookId, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/book/{bookId}/summary")
    @Operation(
            summary = "Get aggregate rating summary for a book",
            description = "Computes and returns the overall average star rating score and total count of reviews for a book."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Rating summary computed and returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookReviewSummaryDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<BookReviewSummaryDTO> getBookReviewSummary(
            @Parameter(description = "Target book identifier", example = "1", required = true)
            @PathVariable Long bookId) {
        BookReviewSummaryDTO summary = bookReviewService.getBookReviewSummary(bookId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/my")
    @Operation(
            summary = "Get reviews authored by currently authenticated patron",
            description = "Fetches a paginated history of all book reviews posted by the currently logged-in user."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Personal reviews returned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<PageResponse<BookReviewDTO>> getMyReviews(
            @Parameter(description = "Page index (0-based)", example = "0", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10", required = false)
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort attribute", example = "createdAt", required = false)
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)", schema = @Schema(allowableValues = {"ASC", "DESC"}), example = "DESC", required = false)
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) throws Exception {
        PageResponse<BookReviewDTO> response = bookReviewService.getMyReviews(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Get reviews authored by a specific user ID",
            description = "Retrieves a paginated list of reviews written by a specific user identifier."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User reviews returned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<PageResponse<BookReviewDTO>> getReviewsByUserId(
            @Parameter(description = "Author user identifier", example = "5", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Page index", example = "0", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10", required = false)
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort attribute", example = "createdAt", required = false)
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)", schema = @Schema(allowableValues = {"ASC", "DESC"}), example = "DESC", required = false)
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        PageResponse<BookReviewDTO> response = bookReviewService.getReviewsByUserId(userId, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }
}
