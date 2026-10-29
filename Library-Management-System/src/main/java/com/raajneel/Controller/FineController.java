package com.raajneel.Controller;

import com.raajneel.Domain.FineStatus;
import com.raajneel.Domain.FineType;
import com.raajneel.Payload.DTO.FineDTO;
import com.raajneel.Payload.Request.CreateFineRequest;
import com.raajneel.Payload.Request.WaiveFineRequest;
import com.raajneel.Payload.Response.ApiResponse;
import com.raajneel.Payload.Response.PageResponse;
import com.raajneel.Payload.Response.PaymentInitiateResponse;
import com.raajneel.Service.FineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fines")
@Tag(name = "Fines & Penalties", description = "Endpoints for managing, calculating, settling, and waiving patron fines and overdue penalties")
@SecurityRequirement(name = "bearerAuth")
public class FineController {

    private final FineService fineService;

    @PostMapping
    @Operation(
            summary = "Assess and create an administrative fine (Admin / Staff)",
            description = "Creates a new financial penalty associated with a specific book loan (e.g. for damage, loss, or special penalty)."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Fine assessed and saved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FineDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid loan ID, negative amount, or validation failure",
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
    public ResponseEntity<FineDTO> createFine(
            @Valid @RequestBody CreateFineRequest fineRequest) throws Exception {
        FineDTO fineDTO = fineService.createFine(fineRequest);
        return ResponseEntity.ok(fineDTO);
    }

    @PostMapping("/{id}/pay")
    @Operation(
            summary = "Initiate payment settlement for a fine",
            description = "Generates a gateway checkout payment order to settle the outstanding balance of the specified fine ID."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Payment order initiated successfully; checkout order details returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentInitiateResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Fine ID not found, already paid, or waived",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Payment gateway communication failure",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<PaymentInitiateResponse> payFine(
            @Parameter(description = "Fine identifier to pay", example = "201", required = true)
            @PathVariable Long id,
            @Parameter(description = "Optional pre-existing external transaction ID", example = "TXN_manual_12345", required = false)
            @RequestParam(required = false) String transactionId) throws Exception {
        PaymentInitiateResponse res = fineService.payFine(id, transactionId);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/waive")
    @Operation(
            summary = "Waive a fine penalty (Admin)",
            description = "Marks an assessed fine as WAIVED, records the waiving administrator ID, timestamp, and justification reason."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Fine successfully waived",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FineDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Fine not found or already settled",
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
    public ResponseEntity<FineDTO> waiveFine(
            @Valid @RequestBody WaiveFineRequest waiveFineRequest) throws Exception {
        FineDTO fineDTO = fineService.waiveFine(waiveFineRequest);
        return ResponseEntity.ok(fineDTO);
    }

    @GetMapping("/my")
    @Operation(
            summary = "Get current authenticated patron's fines",
            description = "Retrieves all fines incurred by the currently logged-in user, optionally filtered by status (PENDING, PAID, WAIVED) or fine type."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "List of patron's fines returned successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FineDTO.class)))
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
    public ResponseEntity<List<FineDTO>> getMyFines(
            @Parameter(description = "Filter by fine settlement status", schema = @Schema(allowableValues = {"PENDING", "PARTIALLY_PAID", "PAID", "WAIVED"}), example = "PENDING", required = false)
            @RequestParam(required = false) FineStatus status,
            @Parameter(description = "Filter by fine violation type", schema = @Schema(allowableValues = {"OVERDUE", "DAMAGE", "LOSS", "PROCESSING"}), example = "OVERDUE", required = false)
            @RequestParam(required = false) FineType type) throws Exception {
        List<FineDTO> fines = fineService.getMyFine(status, type);
        return ResponseEntity.ok(fines);
    }

    @GetMapping
    @Operation(
            summary = "Search and list all fines across system (Admin / Staff)",
            description = "Retrieves a paginated list of all system fines with optional filters for fine status, type, and specific user ID."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Paginated list of system fines returned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))
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
    public ResponseEntity<PageResponse<FineDTO>> getAllFines(
            @Parameter(description = "Filter by fine status", schema = @Schema(allowableValues = {"PENDING", "PARTIALLY_PAID", "PAID", "WAIVED"}), example = "PENDING", required = false)
            @RequestParam(required = false) FineStatus status,
            @Parameter(description = "Filter by fine type", schema = @Schema(allowableValues = {"OVERDUE", "DAMAGE", "LOSS", "PROCESSING"}), example = "OVERDUE", required = false)
            @RequestParam(required = false) FineType type,
            @Parameter(description = "Filter by patron user ID", example = "5", required = false)
            @RequestParam(required = false) Long userId,
            @Parameter(description = "Page number (0-based)", example = "0", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size limit", example = "20", required = false)
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<FineDTO> fines = fineService.getAllFine(status, type, userId, page, size);
        return ResponseEntity.ok(fines);
    }
}
