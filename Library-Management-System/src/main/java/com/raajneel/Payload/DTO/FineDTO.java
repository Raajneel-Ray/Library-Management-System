package com.raajneel.Payload.DTO;

import com.raajneel.Domain.FineStatus;
import com.raajneel.Domain.FineType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FineDTO {
    private Long id;

    @NotNull(message = "UserId is mandatory")
    private Long userId;

    private String userName;

    private String userEmail;

    @NotNull(message = "Book Loan Id is mandatory")
    private Long bookLoanId;

    private String bookTitle;

    private String bookIsbn;

    @NotNull(message = "Fine Type is mandatory")
    private FineType type;

    @PositiveOrZero(message = "Fine amount cannot be zero")
    private Long amount;

    private Long amountPaid;

    private Long amountOutstanding;

    @NotNull(message = "Fine Status is mandatory")
    private FineStatus status;

    private String reason;

    private String notes;

    private Long waivedByUserId;

    private String waivedByUserName;

    private LocalDateTime waivedAt;

    private String waiverReason;

    private LocalDateTime paidAt;

    private Long processedByUserId;

    private String processByUserName;

    private String transactionId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
