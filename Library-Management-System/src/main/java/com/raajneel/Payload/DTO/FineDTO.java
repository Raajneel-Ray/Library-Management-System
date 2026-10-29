package com.raajneel.Payload.DTO;

import com.raajneel.Domain.FineStatus;
import com.raajneel.Domain.FineType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "Data transfer object representing a financial fine or penalty charge")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FineDTO {

    @Schema(description = "Unique identifier of the fine entry", example = "201", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @NotNull(message = "UserId is mandatory")
    @Schema(description = "Identifier of the user charged with the fine", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "Name of the penalized user", example = "Alice Johnson", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String userName;

    @Schema(description = "Email of the penalized user", example = "alice.johnson@example.com", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String userEmail;

    @NotNull(message = "Book Loan Id is mandatory")
    @Schema(description = "Identifier of the associated book loan that incurred this fine", example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long bookLoanId;

    @Schema(description = "Title of the associated book", example = "Clean Code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bookTitle;

    @Schema(description = "ISBN of the associated book", example = "978-0132350884", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bookIsbn;

    @NotNull(message = "Fine Type is mandatory")
    @Schema(description = "Reason or category of the fine", allowableValues = {"OVERDUE", "DAMAGE", "LOSS", "PROCESSING"}, example = "OVERDUE", requiredMode = Schema.RequiredMode.REQUIRED)
    private FineType type;

    @PositiveOrZero(message = "Fine amount cannot be zero")
    @Schema(description = "Total assessment amount in smallest currency unit / cents / paisa", example = "1500", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long amount;

    @Schema(description = "Amount settled toward this fine", example = "1500", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long amountPaid;

    @Schema(description = "Remaining unpaid balance for this fine", example = "0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long amountOutstanding;

    @NotNull(message = "Fine Status is mandatory")
    @Schema(description = "Current payment status of the fine", allowableValues = {"PENDING", "PARTIALLY_PAID", "PAID", "WAIVED"}, example = "PENDING", requiredMode = Schema.RequiredMode.REQUIRED)
    private FineStatus status;

    @Schema(description = "Official cause or violation reason", example = "Book returned 5 days past due date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String reason;

    @Schema(description = "Internal staff remarks or circulation notes", example = "First warning granted previously", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String notes;

    @Schema(description = "Identifier of administrator who waived the fine", example = "2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long waivedByUserId;

    @Schema(description = "Name of administrator who authorized the waiver", example = "Admin Jane", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String waivedByUserName;

    @Schema(description = "Timestamp when the fine waiver was executed", example = "2026-10-25T11:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime waivedAt;

    @Schema(description = "Justification recorded for waiving the penalty", example = "Medical emergency certificate provided", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String waiverReason;

    @Schema(description = "Timestamp when full or final payment was received", example = "2026-10-26T15:30:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime paidAt;

    @Schema(description = "Identifier of staff member or gateway processor user", example = "2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long processedByUserId;

    @Schema(description = "Name of staff member who processed the transaction", example = "Admin Jane", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String processByUserName;

    @Schema(description = "Gateway payment transaction or charge ID", example = "pay_N83kS0v81Lkdfg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String transactionId;

    @Schema(description = "Creation timestamp of the fine record", example = "2026-10-20T09:15:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp of the fine record", example = "2026-10-26T15:30:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime updatedAt;
}
