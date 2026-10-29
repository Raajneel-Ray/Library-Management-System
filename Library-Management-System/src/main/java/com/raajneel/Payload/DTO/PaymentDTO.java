package com.raajneel.Payload.DTO;

import com.raajneel.Domain.PaymentGateway;
import com.raajneel.Domain.PaymentStatus;
import com.raajneel.Domain.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Schema(description = "Data transfer object representing an online payment transaction record")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {

    @Schema(description = "Unique identifier of the payment record", example = "301", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @NotNull(message = "User ID is mandatory")
    @Schema(description = "Identifier of the paying patron", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "Full name of the paying user", example = "Alice Johnson", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String userName;

    @Schema(description = "Email of the paying user", example = "alice.johnson@example.com", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String userEmail;

    @Schema(description = "Associated book loan ID if payment is settling a fine", example = "101", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long bookLoanId;

    @Schema(description = "Associated subscription ID if payment is purchasing a membership plan", example = "12", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long subscriptionId;

    @NotNull(message = "Payment Type is mandatory")
    @Schema(description = "Purpose of payment transaction", allowableValues = {"FINE", "MEMBERSHIP", "LOST_BOOK_PENALTY", "DAMAGED_BOOK_PENALTY", "REFUND"}, example = "FINE", requiredMode = Schema.RequiredMode.REQUIRED)
    private PaymentType paymentType;

    @Schema(description = "Execution status of payment", allowableValues = {"PENDING", "PROCESSING", "SUCCESS", "FAILED", "CANCELLED"}, example = "SUCCESS", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private PaymentStatus status;

    @NotNull(message = "Payment gateway is mandatory")
    @Schema(description = "Selected processing payment gateway provider", allowableValues = {"RAZORPAY", "STRIPE"}, example = "RAZORPAY", requiredMode = Schema.RequiredMode.REQUIRED)
    private PaymentGateway gateway;

    @NotNull(message = "Amount is mandatory")
    @Positive(message = "Amount must be Positive")
    @Schema(description = "Payment charge amount in smallest currency unit (e.g., paisa or cents)", example = "25000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long amount;

    @Schema(description = "System transaction reference identifier", example = "TXN_9876543210", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String transactionId;

    @Schema(description = "Payment provider unique charge or payment ID", example = "pay_N83kS0v81Lkdfg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String gatewayPaymentId;

    @Schema(description = "Payment provider order identifier", example = "order_DBJOWzybf0sJbb", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String gatewayOrderId;

    @Schema(description = "Cryptographic signature verifying payment authenticity", example = "9ef50b4c6935075d3330374704f433f9", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String gatewaySignature;

    @Schema(description = "Payment statement descriptor or checkout notes", example = "Overdue fine payment for Clean Code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Error message or explanation if the transaction failed", example = "Card issuer declined transaction", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String failureReason;

    @Schema(description = "Number of retry attempts executed", example = "0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer retryCount;

    @CreationTimestamp
    @Schema(description = "Timestamp when payment workflow was initiated", example = "2026-10-25T14:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime initiatedAt;

    @Schema(description = "Timestamp when payment successfully finalized or failed", example = "2026-10-25T14:02:15", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime completedAt;

    @CreationTimestamp
    @Schema(description = "Record creation timestamp", example = "2026-10-25T14:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Schema(description = "Record update timestamp", example = "2026-10-25T14:02:15", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime updatedAt;
}
