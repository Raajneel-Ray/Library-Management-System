package com.raajneel.Service.Implementation;

import com.raajneel.Domain.UserRole;
import com.raajneel.Exception.BookException;
import com.raajneel.Exception.BookReviewException;
import com.raajneel.Mapper.BookReviewMapper;
import com.raajneel.Model.Book;
import com.raajneel.Model.BookReview;
import com.raajneel.Model.User;
import com.raajneel.Payload.DTO.BookReviewDTO;
import com.raajneel.Payload.DTO.BookReviewSummaryDTO;
import com.raajneel.Payload.Request.BookReviewRequest;
import com.raajneel.Payload.Response.PageResponse;
import com.raajneel.Repository.BookRepository;
import com.raajneel.Repository.BookReviewRepository;
import com.raajneel.Service.BookReviewService;
import com.raajneel.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookReviewServiceImpl implements BookReviewService {

    private final BookReviewRepository bookReviewRepository;
    private final BookRepository bookRepository;
    private final UserService userService;
    private final BookReviewMapper bookReviewMapper;

    @Override
    @Transactional
    public BookReviewDTO createReview(BookReviewRequest request) throws Exception {
        User currentUser = userService.getCurrentUser();
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookException("Book not found with ID: " + request.getBookId()));

        if (bookReviewRepository.existsByUserIdAndBookId(currentUser.getId(), book.getId())) {
            throw new BookReviewException("You have already submitted a review for this book");
        }

        BookReview review = BookReview.builder()
                .user(currentUser)
                .book(book)
                .rating(request.getRating())
                .reviewText(request.getReviewText())
                .build();

        BookReview savedReview = bookReviewRepository.save(review);
        return bookReviewMapper.toDTO(savedReview);
    }

    @Override
    @Transactional
    public BookReviewDTO updateReview(Long reviewId, BookReviewRequest request) throws Exception {
        BookReview review = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new BookReviewException("Review not found with ID: " + reviewId));

        User currentUser = userService.getCurrentUser();
        boolean isOwner = review.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == UserRole.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new BookReviewException("You can only update your own review");
        }

        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getReviewText() != null) {
            review.setReviewText(request.getReviewText());
        }

        BookReview updatedReview = bookReviewRepository.save(review);
        return bookReviewMapper.toDTO(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) throws Exception {
        BookReview review = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new BookReviewException("Review not found with ID: " + reviewId));

        User currentUser = userService.getCurrentUser();
        boolean isOwner = review.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == UserRole.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new BookReviewException("You can only delete your own review");
        }

        bookReviewRepository.delete(review);
    }

    @Override
    @Transactional(readOnly = true)
    public BookReviewDTO getReviewById(Long reviewId) throws Exception {
        BookReview review = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new BookReviewException("Review not found with ID: " + reviewId));
        return bookReviewMapper.toDTO(review);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookReviewDTO> getReviewsByBookId(Long bookId, int page, int size, String sortBy, String sortDirection) {
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        Page<BookReview> reviewPage = bookReviewRepository.findByBookId(bookId, pageable);
        return buildPageResponse(reviewPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookReviewDTO> getMyReviews(int page, int size, String sortBy, String sortDirection) throws Exception {
        User currentUser = userService.getCurrentUser();
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        Page<BookReview> reviewPage = bookReviewRepository.findByUserId(currentUser.getId(), pageable);
        return buildPageResponse(reviewPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookReviewDTO> getReviewsByUserId(Long userId, int page, int size, String sortBy, String sortDirection) {
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        Page<BookReview> reviewPage = bookReviewRepository.findByUserId(userId, pageable);
        return buildPageResponse(reviewPage);
    }

    @Override
    @Transactional(readOnly = true)
    public BookReviewSummaryDTO getBookReviewSummary(Long bookId) {
        Double avgRating = bookReviewRepository.getAverageRatingByBookId(bookId);
        long count = bookReviewRepository.countByBookId(bookId);

        double roundedAvg = 0.0;
        if (avgRating != null) {
            roundedAvg = Math.round(avgRating * 10.0) / 10.0;
        }

        return BookReviewSummaryDTO.builder()
                .bookId(bookId)
                .averageRating(roundedAvg)
                .totalReviews(count)
                .build();
    }

    private Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
        int validatedSize = Math.max(1, Math.min(size, 100));
        String validSortBy = (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt";
        Sort sort = "ASC".equalsIgnoreCase(sortDirection)
                ? Sort.by(validSortBy).ascending()
                : Sort.by(validSortBy).descending();
        return PageRequest.of(Math.max(0, page), validatedSize, sort);
    }

    private PageResponse<BookReviewDTO> buildPageResponse(Page<BookReview> reviewPage) {
        List<BookReviewDTO> dtoList = reviewPage.getContent().stream()
                .map(bookReviewMapper::toDTO)
                .toList();

        return new PageResponse<>(
                dtoList,
                reviewPage.getNumber(),
                reviewPage.getSize(),
                (int) reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.isLast(),
                reviewPage.isFirst(),
                reviewPage.isEmpty()
        );
    }
}
