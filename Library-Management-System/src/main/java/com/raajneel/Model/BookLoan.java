package com.raajneel.Model;

import com.raajneel.Domain.BookLoanStatus;
import com.raajneel.Domain.BookLoanType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_loans")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BookLoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Native MySQL auto-increment instead of table-based sequence emulation
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false,length = 20)
    private BookLoanType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false,length = 20)
    private BookLoanStatus status;

    @Column(nullable = false)
    private LocalDate checkoutDate;

    private  LocalDate dueDate;

    private LocalDate returnDate;

    @Column(nullable = false)
    private Integer renewalCount=0;

    @Column(nullable = false)
    private Integer maxRenewals=2;

    // todo : fine entity implementing

    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    private Boolean isOverdue=false;

    @Column(nullable = false)
    private Integer overdueDays=0;

    @Column(nullable = false,updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private  LocalDateTime updatedAt;

    public  boolean isActive(){
        return status==BookLoanStatus.CHECKED_OUT || status==BookLoanStatus.OVERDUE;
    }

    public  boolean canRenew(){
        return  status==BookLoanStatus.CHECKED_OUT && !isOverdue && renewalCount<maxRenewals;
    }
}
