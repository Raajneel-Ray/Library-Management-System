package com.raajneel.Controller;

import com.raajneel.Payload.DTO.PaymentDTO;
import com.raajneel.Payload.Request.PaymentVerifyRequest;
import com.raajneel.Payload.Response.ApiResponse;
import com.raajneel.Service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@Tag(name = "Payments & Transactions", description = "Endpoints for payment gateway signature verification, transaction cancellation, and transaction auditing")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/verify")
    @Operation(
            summary = "Verify gateway payment signature and finalize order",
            description = "Validates Razorpay or Stripe checkout credentials, confirms fund settlement, updates fine or subscription status, and records the transaction receipt."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Payment verified successfully; payment record returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Payment signature invalid or checkout verification failed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal payment processing error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<?> verifyPayment(
            @Valid @RequestBody PaymentVerifyRequest request) {
        try {
            PaymentDTO payment = paymentService.verifyPayment(request);
            return ResponseEntity.ok().body(payment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        }
    }

    @PostMapping("/{paymentId}/cancel")
    @Operation(
            summary = "Cancel a pending payment transaction",
            description = "Cancels a pending payment transaction record and marks its status as CANCELLED."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Payment cancelled successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Payment ID not found or payment already finalized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server processing failure",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<?> cancelPayment(
            @Parameter(description = "Payment record identifier to cancel", example = "301", required = true)
            @PathVariable Long paymentId) {
        try {
            PaymentDTO payment = paymentService.cancelPayment(paymentId);
            return ResponseEntity.ok().body(payment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        }
    }

    @GetMapping
    @Operation(
            summary = "Get paginated transaction history (Admin / Staff)",
            description = "Retrieves an administrative paginated log of all gateway payments across the system with sorting support."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Paginated payment transactions returned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Requires ROLE_ADMIN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server processing failure",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<Page<PaymentDTO>> getAllPayments(
            @Parameter(description = "Page index (0-based)", example = "0", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size limit", example = "10", required = false)
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Attribute to sort by", example = "createdAt", required = false)
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort ordering direction (ASC or DESC)", schema = @Schema(allowableValues = {"ASC", "DESC"}), example = "DESC", required = false)
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PaymentDTO> payments = paymentService.getAllPayments(pageable);
        return ResponseEntity.ok(payments);
    }
}
