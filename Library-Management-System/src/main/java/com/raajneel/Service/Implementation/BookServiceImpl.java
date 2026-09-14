package com.raajneel.Service.Implementation;

import com.raajneel.Exception.BookException;
import com.raajneel.Mapper.BookMapper;
import com.raajneel.Model.Book;
import com.raajneel.Payload.DTO.BookDTO;
import com.raajneel.Payload.Request.BookSearchRequest;
import com.raajneel.Payload.Response.PageResponse;
import com.raajneel.Repository.BookRepository;
import com.raajneel.Service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDTO createBook(BookDTO bookDTO) throws BookException {
        if(bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new BookException("book with ISBN "+bookDTO.getIsbn()+" already exists");
        }
        Book book = bookMapper.toEntity(bookDTO);
        book.isAvailableCopiesValid();
        Book savedBook = bookRepository.save(book);

        return bookMapper.toBookDTO(savedBook);
    }

    @Override
    public List<BookDTO> createBooksBulk(List<BookDTO> bookDTOS) throws BookException {
        List<BookDTO> createdBooks = new ArrayList<>();
        for(BookDTO bookDTO : bookDTOS) {
            BookDTO dto = createBook(bookDTO);
            createdBooks.add(dto);
        }
        return createdBooks;
    }

    @Override
    public BookDTO getBookById(Long bookId) throws BookException {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new BookException("book not found!"));
        return bookMapper.toBookDTO(book);
    }

    @Override
    public BookDTO getBookByISBN(String isbn) throws BookException {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(()-> new BookException("book not found!"));
        return bookMapper.toBookDTO(book);
    }

    @Override
    public BookDTO updateBook(Long bookId, BookDTO bookDTO) throws BookException {
        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(()-> new BookException("existingBook not found!"));
        bookMapper.updateEntityFromDTO(bookDTO, existingBook);
        Book updatedBook = bookRepository.save(existingBook);
        return bookMapper.toBookDTO(updatedBook);
    }

    @Override
    public void deleteBook(Long bookId) throws BookException {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new BookException("book not found!"));
        book.setActive(false);
        bookRepository.save(book);
    }

    @Override
    public void hardDeleteBook(Long bookId) throws BookException {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new BookException("book not found!"));
        bookRepository.delete(book);
    }

    @Override
    public PageResponse<BookDTO> searchBooksWithFilters(BookSearchRequest bookSearchRequest) {
        return null;
    }

    @Override
    public long getTotalActiveBooks() {
        return bookRepository.countByActiveTrue();
    }

    @Override
    public long getTotalAvailableBooks() {
        return bookRepository.countAvailableBooks();
    }
}
