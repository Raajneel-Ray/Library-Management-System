package com.raajneel.Payload.DTO;

import com.raajneel.Domain.BookLoanStatus;
import com.raajneel.Domain.BookLoanType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Data transfer object representing a book loan or checkout transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookLoanDTO {

    @Schema(description = "Unique identifier of the book loan", example = "101", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @Schema(description = "Identifier of the borrower user", example = "5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long userId;

    @Schema(description = "Full name of the borrower", example = "Alice Johnson", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String userName;

    @Schema(description = "Email address of the borrower", example = "alice.johnson@example.com", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String userEmail;

    @Schema(description = "Identifier of the checked-out book", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long bookId;

    @Schema(description = "Title of the checked-out book", example = "Clean Code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bookTitle;

    @Schema(description = "ISBN number of the checked-out book", example = "978-0132350884", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bookIsbn;

    @Schema(description = "Author of the checked-out book", example = "Robert C. Martin", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bookAuthor;

    @Schema(description = "Direct URL to book cover art", example = "https://images.example.com/books/cleancode.jpg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bookCoverImage;

    @Schema(description = "Type of loan transaction", allowableValues = {"CHECKOUT", "RENEWAL", "RETURN"}, example = "CHECKOUT", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BookLoanType type;

    @Schema(description = "Current lifecycle status of the loan", allowableValues = {"CHECKED_OUT", "RETURNED", "OVERDUE", "LOST", "DAMAGED"}, example = "CHECKED_OUT", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BookLoanStatus status;

    @Schema(description = "Date when the book was checked out", example = "2026-10-15", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDate checkoutDate;

    @Schema(description = "Official due date for return", example = "2026-10-29", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDate dueDate;

    @Schema(description = "Number of calendar days remaining until loan is overdue (negative if already past due)", example = "3", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long remainingDays;

    @Schema(description = "Date on which the book was checked back in", example = "2026-10-28", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDate returnDate;

    @Schema(description = "Number of times the loan has been extended/renewed", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer renewalCount;

    @Schema(description = "Maximum renewals permitted under active subscription plan", example = "2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer maxRenewals;

    @Schema(description = "Calculated fine amount in currency units if overdue or damaged", example = "5.00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BigDecimal fineAmount;

    @Schema(description = "Whether assessed fines for this loan have been settled", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean finePaid;

    @Schema(description = "Circulation or administrative remarks", example = "Book returned in pristine condition", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String notes;

    @Schema(description = "Flag indicating if the loan has exceeded its return due date", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean isOverdue;

    @Schema(description = "Elapsed days past the due date", example = "0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer overdueDays;

    @Schema(description = "Record creation timestamp", example = "2026-10-15T09:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime createdAt;

    @Schema(description = "Record last modification timestamp", example = "2026-10-20T11:20:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime updatedAt;
}
