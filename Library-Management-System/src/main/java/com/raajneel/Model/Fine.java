package com.raajneel.Model;

import com.raajneel.Domain.FineStatus;
import com.raajneel.Domain.FineType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fines")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class Fine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private BookLoan bookLoan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    private FineType fineType;

    @Column(nullable = false)
    private Long amount;

    private FineStatus status;

    @Column(length=500)
    private String reason;

    @Column(length = 1000)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    private User waivedBy;

    @Column(name = "waived_at")
    private LocalDateTime waivedAt;

    @Column(name = "Waiver_reason",length = 500)
    private String waiverReason;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_user_id")
    private User processedBy;

    @Column(name = "transaction_id",length = 100)
    private String transactionId;

    @Column(nullable = false,updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public  void  applyPayment(Long paymentAmount){
        if (paymentAmount==null || paymentAmount<=0){
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        this.status=FineStatus.PAID;
        this.paidAt=LocalDateTime.now();
    }

    public void  waive(User admin, String reason){
        this.status=FineStatus.WAIVED;
        this.waivedBy=admin;
        this.waivedAt=LocalDateTime.now();
        this.waiverReason=reason;
    }
}
