package com.raajneel.Controller;

import com.raajneel.Domain.BookLoanStatus;
import com.raajneel.Payload.DTO.BookLoanDTO;
import com.raajneel.Payload.Request.BookLoanSearchRequest;
import com.raajneel.Payload.Request.CheckinRequest;
import com.raajneel.Payload.Request.CheckoutRequest;
import com.raajneel.Payload.Request.RenewRequest;
import com.raajneel.Payload.Response.ApiResponse;
import com.raajneel.Payload.Response.PageResponse;
import com.raajneel.Service.BookLoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-loans")
@Tag(name = "Book Loans & Circulation", description = "Endpoints for borrowing, returning, renewing, and tracking book loan transactions")
@SecurityRequirement(name = "bearerAuth")
public class BookLoanController {

    private final BookLoanService bookLoanService;

    @PostMapping("/checkout")
    @Operation(
            summary = "Checkout a book for current authenticated patron",
            description = "Creates an active checkout loan for the authenticated patron. Validates active membership plan, max allowed concurrent loans, overdue debt, and book copy availability."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Book checked out successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookLoanDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Checkout failed - book unavailable, max limit reached, or unresolved overdue loans",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - valid JWT required",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server processing failure",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<BookLoanDTO> checkoutBook(
            @Valid @RequestBody CheckoutRequest checkoutRequest) throws Exception {
        BookLoanDTO bookLoan = bookLoanService.checkoutBook(checkoutRequest);
        return new ResponseEntity<>(bookLoan, HttpStatus.CREATED);
    }

    @PostMapping("/checkout/user/{userId}")
    @Operation(
            summary = "Checkout a book for a specific user (Staff / Admin)",
            description = "Allows circulation desk staff or administrators to initiate a book checkout on behalf of a designated patron by user ID."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Book checked out successfully on behalf of patron",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookLoanDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "User or book not found, patron loan limit reached, or book out of stock",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
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
                    description = "Internal server processing error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<BookLoanDTO> checkoutBookForUser(
            @Parameter(description = "Identifier of patron receiving checkout", example = "5", required = true)
            @PathVariable Long userId,
            @Valid @RequestBody CheckoutRequest checkoutRequest) throws Exception {
        BookLoanDTO bookLoan = bookLoanService.checkoutBookForUser(userId, checkoutRequest);
        return new ResponseEntity<>(bookLoan, HttpStatus.CREATED);
    }

    @PostMapping("/checkin")
    @Operation(
            summary = "Check in and return a borrowed book",
            description = "Records the physical return of a borrowed book. Increments available inventory copies unless reported lost, marks loan as RETURNED, and computes any overdue penalties."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Book successfully checked in and inventory reconciled",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookLoanDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Book loan not found or not in active status",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error during return processing",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<BookLoanDTO> checkin(
            @Valid @RequestBody CheckinRequest checkinRequest) throws Exception {
        BookLoanDTO bookLoan = bookLoanService.checkinBook(checkinRequest);
        return new ResponseEntity<>(bookLoan, HttpStatus.OK);
    }

    @PostMapping("/renew")
    @Operation(
            summary = "Renew an active book loan",
            description = "Extends the due date of an active loan by requested extension days if loan is not overdue and renewalCount has not reached maxRenewals."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Book loan successfully renewed and due date extended",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookLoanDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Renewal disallowed - max renewals reached, book loan overdue, or not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<BookLoanDTO> renew(
            @Valid @RequestBody RenewRequest renewalRequest) throws Exception {
        BookLoanDTO bookLoan = bookLoanService.renewCheckout(renewalRequest);
        return new ResponseEntity<>(bookLoan, HttpStatus.OK);
    }

    @GetMapping("/mybookloans")
    @Operation(
            summary = "Get current authenticated patron's loans",
            description = "Retrieves a paginated list of book loans associated with the logged-in user, optionally filtered by status (e.g., CHECKED_OUT, OVERDUE, RETURNED)."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Patron book loans returned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server failure",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<PageResponse<BookLoanDTO>> getMyBookLoans(
            @Parameter(description = "Optional filter by loan status (CHECKED_OUT, RETURNED, OVERDUE, LOST, DAMAGED)", schema = @Schema(allowableValues = {"CHECKED_OUT", "RETURNED", "OVERDUE", "LOST", "DAMAGED"}), example = "CHECKED_OUT", required = false)
            @RequestParam(required = false) BookLoanStatus status,
            @Parameter(description = "Zero-based page index", example = "0", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20", required = false)
            @RequestParam(defaultValue = "20") int size) throws Exception {
        PageResponse<BookLoanDTO> bookLoans = bookLoanService.getMyBookLoans(status, page, size);
        return ResponseEntity.ok(bookLoans);
    }

    @PostMapping("/search")
    @Operation(
            summary = "Search and filter all book loans (Staff / Admin)",
            description = "Performs an administrative search across all system book loans with filters for patron ID, book ID, loan status, date range, and overdue flag."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Paginated list of matching book loans",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid search payload",
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
    public ResponseEntity<PageResponse<BookLoanDTO>> getAllBookLoans(
            @RequestBody BookLoanSearchRequest searchRequest) {
        PageResponse<BookLoanDTO> bookLoans = bookLoanService.getBookLoans(searchRequest);
        return ResponseEntity.ok(bookLoans);
    }

    @PostMapping("/admin/update-overdue")
    @Operation(
            summary = "Batch update overdue statuses across all loans (Admin)",
            description = "Triggers a system-wide reconciliation job that scans active checked-out loans against current date and transitions past-due loans to OVERDUE status with calculated overdueDays."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Overdue batch job completed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
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
    public ResponseEntity<ApiResponse> updateOverdueBookLoans() {
        int updateCount = bookLoanService.updateOverdueBookLoan();
        return ResponseEntity.ok(new ApiResponse("Overdue book loans are updated. Modified: " + updateCount, true));
    }
}
