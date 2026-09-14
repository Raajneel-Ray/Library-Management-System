package com.raajneel.Service;

import com.raajneel.Exception.BookException;
import com.raajneel.Payload.DTO.BookDTO;
import com.raajneel.Payload.Request.BookSearchRequest;
import com.raajneel.Payload.Response.PageResponse;

import java.util.List;

public interface BookService {

    BookDTO createBook(BookDTO bookDTO) throws BookException;

    List<BookDTO> createBooksBulk(List<BookDTO> bookDTOS) throws BookException;

    BookDTO getBookById(Long bookId) throws BookException;

    BookDTO getBookByISBN(String isbn) throws BookException;

    BookDTO updateBook(Long bookId, BookDTO bookDTO) throws BookException;

    void deleteBook(Long bookId) throws BookException;

    void hardDeleteBook(Long bookId) throws BookException;

    PageResponse<BookDTO> searchBooksWithFilters(
            BookSearchRequest bookSearchRequest
    );
    long getTotalActiveBooks();

    long getTotalAvailableBooks();

}
