package com.raajneel.Controller;

import com.raajneel.Exception.SubscriptionException;
import com.raajneel.Payload.DTO.SubscriptionDTO;
import com.raajneel.Payload.Response.ApiResponse;
import com.raajneel.Payload.Response.PaymentInitiateResponse;
import com.raajneel.Service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
@Tag(name = "Subscriptions & Memberships", description = "Endpoints for purchasing, activating, managing, cancelling, and inspecting library patron subscriptions")
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    @Operation(
            summary = "Initiate new plan subscription purchase",
            description = "Creates a pending subscription for the user and initializes a gateway checkout order returning payment gateway details."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Subscription order created; payment initiation details returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentInitiateResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid plan ID or user already has an active subscription",
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
    public ResponseEntity<PaymentInitiateResponse> subscribe(
            @RequestBody SubscriptionDTO subscriptionDTO) throws Exception {
        PaymentInitiateResponse dto = subscriptionService.subscribe(subscriptionDTO);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping("/admin")
    @Operation(
            summary = "List all subscriptions (Admin)",
            description = "Retrieves an administrative list of all patron subscriptions across the library system."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "List of all user subscriptions returned successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SubscriptionDTO.class)))
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
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<List<SubscriptionDTO>> getAllSubscriptions() {
        Pageable pageable = PageRequest.of(0, 10);
        List<SubscriptionDTO> list = subscriptionService.getAllSubscription(pageable);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/user/active-subscriptions")
    @Operation(
            summary = "Get active subscription for user",
            description = "Retrieves the currently active, unexpired subscription record for the logged-in user or a specified user ID."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Active subscription found and returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "No active subscription found for user",
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
    public ResponseEntity<SubscriptionDTO> getUsersActiveSubscription(
            @Parameter(description = "Optional user ID (defaults to currently logged-in user)", example = "5", required = false)
            @RequestParam(required = false) Long userId) throws Exception {
        SubscriptionDTO dto = subscriptionService.getUsersActiveSubscription(userId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/admin/deactivate-expired")
    @Operation(
            summary = "Deactivate expired subscriptions (Admin batch job)",
            description = "Triggers a batch update task that marks all subscriptions past their endDate as isActive = false."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Batch deactivation completed successfully",
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
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse> deactivateExpiredSubscriptions() {
        subscriptionService.deactivateExpiredSubscription();
        ApiResponse response = new ApiResponse("Task done successfully!", true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel/{subscriptionId}")
    @Operation(
            summary = "Cancel an active subscription",
            description = "Cancels an active subscription membership with an optional recorded justification reason."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Subscription successfully cancelled",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Subscription ID not found or already cancelled",
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
    public ResponseEntity<SubscriptionDTO> cancelSubscription(
            @Parameter(description = "Identifier of subscription to cancel", example = "12", required = true)
            @PathVariable Long subscriptionId,
            @Parameter(description = "Optional reason for cancellation", example = "Graduating university", required = false)
            @RequestParam(required = false) String reason) throws Exception {
        SubscriptionDTO dto = subscriptionService.cancelSubscription(subscriptionId, reason);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/activate")
    @Operation(
            summary = "Activate subscription following successful payment",
            description = "Activates a subscription tier following verified completion of the associated payment transaction."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Subscription successfully activated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid subscription ID or payment ID",
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
    public ResponseEntity<SubscriptionDTO> activateSubscription(
            @Parameter(description = "Subscription record ID", example = "12", required = true)
            @RequestParam Long subscriptionId,
            @Parameter(description = "Verified payment record ID", example = "301", required = true)
            @RequestParam Long paymentId) throws Exception {
        SubscriptionDTO dto = subscriptionService.activateSubscription(subscriptionId, paymentId);
        return ResponseEntity.ok(dto);
    }
}
