package com.raajneel.Repository;

import com.raajneel.Domain.BookLoanStatus;
import com.raajneel.Model.BookLoan;
import com.raajneel.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {
    Page<BookLoan> findByUserId(Long userId, Pageable pageable);
    Page<BookLoan> findByStatusAndUser(BookLoanStatus status, User user, Pageable pageable);
    Page<BookLoan> findByStatus(BookLoanStatus status, Pageable pageable);
    Page<BookLoan> findByBookId(Long bookId, Pageable pageable);
    List<BookLoan> findByBookId(Long bookId);

    // checks whether a user has checked out a book(+overdue)
    @Query("select case when count(bl) > 0 then true else false end from BookLoan bl " +
            "where bl.user.id = :userId and bl.book.id = :bookId " +
            "and (bl.status = 'CHECKED_OUT' OR bl.status = 'OVERDUE')")
    boolean hasActiveCheckOut(@Param("userId") Long userId, @Param("bookId") Long bookId);

    // returns the number of books that a user has borrowed
    @Query("SELECT COUNT(bl) FROM BookLoan bl WHERE bl.user.id = :userId " +
            "AND (bl.status = 'CHECKED_OUT' OR bl.status = 'OVERDUE')")
    long countActiveBookLoansByUser(@Param("userId") Long userId);

    // returns the number of books that user borrowed which got overdue
    @Query("SELECT COUNT(bl) FROM BookLoan bl WHERE bl.user.id = :userId " +
            "AND bl.status = 'OVERDUE'")
    long countOverdueBookLoansByUser(@Param("userId") Long userId);

    // returns the book that are in overdue state and checked out (to change the status to Overdue) w.r.t due date < today
    @Query("SELECT bl FROM BookLoan bl WHERE bl.dueDate < :currentDate " +
            "AND (bl.status = 'CHECKED_OUT' OR bl.status = 'OVERDUE')")
    Page<BookLoan> findOverdueBookLoans(@Param("currentDate") LocalDate currentDate, Pageable pageable);

    // it finds book loans by data range
    @Query("SELECT bl FROM BookLoan bl WHERE bl.checkoutDate BETWEEN :startDate AND :endDate")
    Page<BookLoan> findBookLoansByDateRange(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate,
                                            Pageable pageable);

    // check whether a particular status for a book for an user
    @Query("SELECT CASE WHEN COUNT(bl) > 0 THEN true ELSE false END FROM BookLoan bl " +
            "WHERE bl.user.id = :userId AND bl.book.id = :bookId AND bl.status = :status")
    boolean existsByUserIdAndBookIdAndStatus(
            @Param("userId") Long userId,
            @Param("bookId") Long bookId,
            @Param("status") BookLoanStatus status
    );

}
