package com.raajneel.Controller;

import com.raajneel.Domain.ReservationStatus;
import com.raajneel.Payload.DTO.ReservationDTO;
import com.raajneel.Payload.Request.ReservationRequest;
import com.raajneel.Payload.Request.ReservationSearchRequest;
import com.raajneel.Payload.Response.ApiResponse;
import com.raajneel.Payload.Response.PageResponse;
import com.raajneel.Service.ReservationService;
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
@RequestMapping("/api/reservations")
@Tag(name = "Reservations & Queue Holds", description = "Endpoints for reserving currently checked-out books, managing hold queues, and fulfilling holds upon availability")
@SecurityRequirement(name = "bearerAuth")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @Operation(
            summary = "Create a book reservation for authenticated patron",
            description = "Places the current authenticated user into the reservation waiting queue for an unavailable book. Checks max active reservation limit (5) and active loan constraints."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Reservation placed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Book already available on shelf, user already has active loan or hold, or limit reached",
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
    public ResponseEntity<ReservationDTO> createReservation(
            @Valid @RequestBody ReservationRequest reservationRequest) throws Exception {
        ReservationDTO reservationDTO = reservationService.createReservation(reservationRequest);
        return ResponseEntity.ok(reservationDTO);
    }

    @PostMapping("/user/{userId}")
    @Operation(
            summary = "Create reservation on behalf of a user (Staff / Admin)",
            description = "Allows library staff to place a reservation hold for a specified patron ID."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Reservation created on behalf of patron",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Target patron not found, max reservation limit exceeded, or book available",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Requires administrative authorization",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server processing failure",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ReservationDTO> createReservationForUser(
            @Parameter(description = "Identifier of patron receiving reservation hold", example = "5", required = true)
            @PathVariable Long userId,
            @Valid @RequestBody ReservationRequest reservationRequest
    ) throws Exception {
        ReservationDTO reservation = reservationService.createReservationForUser(reservationRequest, userId);
        return new ResponseEntity<>(reservation, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Cancel an active reservation hold",
            description = "Cancels an existing pending or available reservation. Can be triggered by the hold owner or by an administrator."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Reservation cancelled successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Reservation not found or cannot be cancelled in current status",
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
    public ResponseEntity<ReservationDTO> cancelReservation(
            @Parameter(description = "Identifier of reservation hold to cancel", example = "401", required = true)
            @PathVariable Long id) throws Exception {
        ReservationDTO reservation = reservationService.cancelReservation(id);
        return ResponseEntity.ok(reservation);
    }

    @PostMapping("/{id}/fulfill")
    @Operation(
            summary = "Fulfill reservation and convert to active checkout loan",
            description = "Transitions an available reservation hold into an active checkout loan for the patron when they pick up the book."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Reservation fulfilled and book loan created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Reservation not eligible for fulfillment or no physical copies in inventory",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
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
    public ResponseEntity<ReservationDTO> fulfillReservation(
            @Parameter(description = "Identifier of reservation hold to fulfill", example = "401", required = true)
            @PathVariable Long id) throws Exception {
        ReservationDTO reservation = reservationService.fulfillReservation(id);
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/my")
    @Operation(
            summary = "Get current authenticated patron's reservations",
            description = "Retrieves a paginated list of reservations placed by the currently logged-in user with filter options."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Patron reservations returned successfully",
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
    public ResponseEntity<PageResponse<ReservationDTO>> getMyReservations(
            @Parameter(description = "Optional filter by status", schema = @Schema(allowableValues = {"PENDING", "AVAILABLE", "FULFILLED", "CANCELLED", "EXPIRED"}), example = "PENDING", required = false)
            @RequestParam(required = false) ReservationStatus status,
            @Parameter(description = "Filter only active holds (PENDING or AVAILABLE)", example = "true", required = false)
            @RequestParam(required = false) Boolean activeOnly,
            @Parameter(description = "Page index (0-based)", example = "0", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size limit", example = "20", required = false)
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort property column", example = "reservedAt", required = false)
            @RequestParam(defaultValue = "reservedAt") String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)", schema = @Schema(allowableValues = {"ASC", "DESC"}), example = "DESC", required = false)
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) throws Exception {
        ReservationSearchRequest searchRequest = new ReservationSearchRequest();
        searchRequest.setStatus(status);
        searchRequest.setActiveOnly(activeOnly);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortDirection(sortDirection);

        PageResponse<ReservationDTO> reservations = reservationService.getMyReservations(searchRequest);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping
    @Operation(
            summary = "Search all system reservations (Admin / Staff)",
            description = "Administrative search across all reservations with filters for patron ID, book ID, reservation status, and hold validity."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Paginated list of reservations matching search criteria",
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
    public ResponseEntity<PageResponse<ReservationDTO>> searchReservations(
            @Parameter(description = "Filter by patron user ID", example = "5", required = false)
            @RequestParam(required = false) Long userId,
            @Parameter(description = "Filter by book identifier", example = "1", required = false)
            @RequestParam(required = false) Long bookId,
            @Parameter(description = "Filter by status", schema = @Schema(allowableValues = {"PENDING", "AVAILABLE", "FULFILLED", "CANCELLED", "EXPIRED"}), example = "PENDING", required = false)
            @RequestParam(required = false) ReservationStatus status,
            @Parameter(description = "Filter only active holds", example = "true", required = false)
            @RequestParam(required = false) Boolean activeOnly,
            @Parameter(description = "Page index (0-based)", example = "0", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size limit", example = "20", required = false)
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort property", example = "reservedAt", required = false)
            @RequestParam(defaultValue = "reservedAt") String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)", schema = @Schema(allowableValues = {"ASC", "DESC"}), example = "DESC", required = false)
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        ReservationSearchRequest searchRequest = new ReservationSearchRequest();
        searchRequest.setUserId(userId);
        searchRequest.setBookId(bookId);
        searchRequest.setStatus(status);
        searchRequest.setActiveOnly(activeOnly);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortDirection(sortDirection);

        PageResponse<ReservationDTO> reservations = reservationService.searchReservations(searchRequest);
        return ResponseEntity.ok(reservations);
    }
}
