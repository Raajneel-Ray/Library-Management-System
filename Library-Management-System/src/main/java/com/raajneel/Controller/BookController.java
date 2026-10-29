package com.raajneel.Controller;

import com.raajneel.Exception.BookException;
import com.raajneel.Payload.DTO.BookDTO;
import com.raajneel.Payload.Request.BookSearchRequest;
import com.raajneel.Payload.Response.ApiResponse;
import com.raajneel.Payload.Response.BookStatsResponse;
import com.raajneel.Payload.Response.PageResponse;
import com.raajneel.Service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/book", "/api/books"})
@Tag(name = "Books", description = "Public and patron-facing endpoints for browsing, searching, and inspecting the book catalog")
@SecurityRequirement(name = "bearerAuth")
public class BookController {

    private final BookService bookService;

    @GetMapping("/{id}")
    @Operation(
            summary = "Get book by ID",
            description = "Retrieves full bibliographic metadata, copy availability, and loan status indicators for a specific book ID."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Book found and returned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid book ID or book not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access - valid JWT token required",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server processing failure",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<BookDTO> getBookById(
            @Parameter(description = "Unique numeric identifier of the book", example = "1", required = true)
            @PathVariable Long id) throws BookException {
        BookDTO bookDTO = bookService.getBookById(id);
        return ResponseEntity.ok(bookDTO);
    }

    @GetMapping("/isbn/{isbn}")
    @Operation(
            summary = "Get book by ISBN",
            description = "Fetches a book's catalog details using its unique International Standard Book Number (ISBN-10 or ISBN-13)."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Book found with matching ISBN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Book not found for provided ISBN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<BookDTO> getBookByIsbn(
            @Parameter(description = "13-digit or 10-digit ISBN code", example = "978-0132350884", required = true)
            @PathVariable String isbn) throws BookException {
        BookDTO bookDTO = bookService.getBookByISBN(isbn);
        return ResponseEntity.ok(bookDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete book by ID",
            description = "Deactivates a book title in the catalog (sets active = false) so it no longer appears in general searches."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Book successfully deactivated (soft-deleted)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Book ID not found or active loans prevent deletion",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server failure",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse> deleteBook(
            @Parameter(description = "Unique numeric identifier of the book to soft delete", example = "1", required = true)
            @PathVariable Long id) throws BookException {
        bookService.deleteBook(id);
        return ResponseEntity.ok(new ApiResponse("Book deleted successfully", true));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search books via query parameters",
            description = "Filters book titles by genre, availability status, and active status with configurable pagination and sorting."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Paginated list of books matching search parameters",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server failure",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<PageResponse<BookDTO>> searchBooks(
            @Parameter(description = "Filter by genre identifier", example = "3", required = false)
            @RequestParam(required = false) Long genreId,
            @Parameter(description = "Filter to only books with available copies > 0", example = "false", required = false)
            @RequestParam(required = false, defaultValue = "false") Boolean availableOnly,
            @Parameter(description = "Filter to only active (non-soft-deleted) books", example = "true", required = false)
            @RequestParam(defaultValue = "true") Boolean activeOnly,
            @Parameter(description = "Page index (0-based)", example = "0", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (records per page)", example = "20", required = false)
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Property column to sort by", example = "createdAt", required = false)
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)", example = "DESC", required = false)
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        BookSearchRequest bookSearchRequest = new BookSearchRequest();
        bookSearchRequest.setGenreId(genreId);
        bookSearchRequest.setAvailableOnly(availableOnly);
        bookSearchRequest.setPage(page);
        bookSearchRequest.setSize(size);
        bookSearchRequest.setSortBy(sortBy);
        bookSearchRequest.setSortDirection(sortDirection);

        PageResponse<BookDTO> books = bookService.searchBooksWithFilters(bookSearchRequest);
        return ResponseEntity.ok(books);
    }

    @PostMapping("/search")
    @Operation(
            summary = "Advanced book search with JSON filter body",
            description = "Performs an advanced multi-attribute search across book catalog with keyword searchTerm, genreId, availability flags, and pagination."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Paginated book search results",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid search criteria body",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server processing failure",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<PageResponse<BookDTO>> advancedSearch(
            @RequestBody BookSearchRequest bookSearchRequest) {
        PageResponse<BookDTO> books = bookService.searchBooksWithFilters(bookSearchRequest);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/stats")
    @Operation(
            summary = "Get overall catalog inventory statistics",
            description = "Returns summary counts of total active catalog titles and aggregate shelf copies available for circulation."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Book catalog statistics returned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookStatsResponse.class))
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
    public ResponseEntity<BookStatsResponse> getBookStats() {
        long totalActiveBooks = bookService.getTotalActiveBooks();
        long totalAvailableBooks = bookService.getTotalAvailableBooks();
        BookStatsResponse stats = new BookStatsResponse(totalActiveBooks, totalAvailableBooks);

        return ResponseEntity.ok(stats);
    }
}
