package com.raajneel.Service;

import com.raajneel.Payload.DTO.BookDTO;
import com.raajneel.Payload.Request.BookSearchRequest;
import com.raajneel.Payload.Response.PageResponse;

import java.util.List;

public interface BookService {

    BookDTO createBook(BookDTO bookDTO);

    List<BookDTO> createBooksBulk();

    BookDTO getBookById(Long bookId);

    BookDTO getBookByISBN(String isbn);

    BookDTO updateBook(Long bookId, BookDTO bookDTO);

    void deleteBook(Long bookId);

    void hardDeleteBook(Long bookId);

    PageResponse<BookDTO> searchBooksWithFilters(
            BookSearchRequest bookSearchRequest
    );
    long getTotalActiveBooks();

    long getTotalAvailableBooks();

}
