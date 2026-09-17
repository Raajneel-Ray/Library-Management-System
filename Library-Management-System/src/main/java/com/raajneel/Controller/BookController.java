package com.raajneel.Controller;

import com.raajneel.Exception.BookException;
import com.raajneel.Payload.DTO.BookDTO;
import com.raajneel.Payload.Request.BookSearchRequest;
import com.raajneel.Payload.Response.ApiResponse;
import com.raajneel.Payload.Response.BookStatsResponse;
import com.raajneel.Payload.Response.PageResponse;
import com.raajneel.Service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) throws BookException {
        BookDTO createdBook = bookService.createBook(bookDTO);
        return ResponseEntity.ok(createdBook);
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> createBooksBulk(
            @Valid @RequestBody List<BookDTO> bookDTOS) throws BookException {
        List<BookDTO> createdBooks = bookService.createBooksBulk(bookDTOS);
        return ResponseEntity.ok(createdBooks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) throws BookException {
        BookDTO bookDTO = bookService.getBookById(id);
        return ResponseEntity.ok(bookDTO);
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDTO> getBookByIsbn(@PathVariable String isbn) throws BookException {
        BookDTO bookDTO = bookService.getBookByISBN(isbn);
        return ResponseEntity.ok(bookDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookDTO bookDTO
    )  throws BookException {
        try {
            BookDTO updatedBook = bookService.updateBook(id, bookDTO);
            return ResponseEntity.ok(updatedBook);
        } catch (BookException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteBook(@PathVariable Long id) throws BookException {
        bookService.deleteBook(id);
        return ResponseEntity.ok(new ApiResponse("Book deleted successfully",true));
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<ApiResponse> deleteBookPermanently(@PathVariable Long id) throws BookException {
        bookService.hardDeleteBook(id);
        return ResponseEntity.ok(new ApiResponse("Book permanently deleted successfully",true));
    }

    // search books using search parameter
    @GetMapping("/search")
    public ResponseEntity<PageResponse<BookDTO>> searchBooks(
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false, defaultValue = "false") Boolean availableOnly,
            @RequestParam(defaultValue = "true") Boolean activeOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ){
        // build book search request using query parameters
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
    public ResponseEntity<PageResponse<BookDTO>> advancedSearch(
            @RequestBody BookSearchRequest bookSearchRequest) {
        PageResponse<BookDTO> books = bookService.searchBooksWithFilters(bookSearchRequest);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/stats")
    public ResponseEntity<BookStatsResponse> getBookStats() {
        long totalActiveBooks = bookService.getTotalActiveBooks();
        long totalAvailableBooks = bookService.getTotalAvailableBooks();
        BookStatsResponse stats = new BookStatsResponse(totalActiveBooks, totalAvailableBooks);

        return ResponseEntity.ok(stats);
    }
}
