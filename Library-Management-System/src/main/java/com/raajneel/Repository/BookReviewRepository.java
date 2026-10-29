package com.raajneel.Repository;

import com.raajneel.Model.BookReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookReviewRepository extends JpaRepository<BookReview, Long> {

    Optional<BookReview> findByUserIdAndBookId(Long userId, Long bookId);

    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    Page<BookReview> findByBookId(Long bookId, Pageable pageable);

    Page<BookReview> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT AVG(CAST(r.rating AS double)) FROM BookReview r WHERE r.book.id = :bookId")
    Double getAverageRatingByBookId(@Param("bookId") Long bookId);

    long countByBookId(Long bookId);

    long countByUserId(Long userId);

    void deleteByUserIdAndBookId(Long userId, Long bookId);
}
