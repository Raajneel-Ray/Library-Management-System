package com.raajneel.Repository;

import com.raajneel.Domain.FineStatus;
import com.raajneel.Domain.FineType;
import com.raajneel.Model.Fine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {

    List<Fine> findByUserId(Long userId);

    List<Fine> findByBookLoanId(Long bookLoanId);

    @Query("SELECT f FROM Fine f WHERE " +
            "f.user.id = :userId AND " +
            "(:status IS NULL OR f.status = :status) AND " +
            "(:type IS NULL OR f.fineType = :type) " +
            "ORDER BY f.createdAt DESC")
    List<Fine> findByUserIdAndFilters(
            @Param("userId") Long userId,
            @Param("status") FineStatus status,
            @Param("type") FineType type
    );

    @Query(value = "SELECT f FROM Fine f WHERE " +
            "(:status IS NULL OR f.status = :status) AND " +
            "(:type IS NULL OR f.fineType = :type) AND " +
            "(:userId IS NULL OR f.user.id = :userId)",
           countQuery = "SELECT COUNT(f) FROM Fine f WHERE " +
            "(:status IS NULL OR f.status = :status) AND " +
            "(:type IS NULL OR f.fineType = :type) AND " +
            "(:userId IS NULL OR f.user.id = :userId)")
    Page<Fine> findFinesWithFilter(
            @Param("status") FineStatus status,
            @Param("type") FineType type,
            @Param("userId") Long userId,
            Pageable pageable
    );

    long countByStatus(FineStatus status);

    long countByUserIdAndStatus(Long userId, FineStatus status);
}
