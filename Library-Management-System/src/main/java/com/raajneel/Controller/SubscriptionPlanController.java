package com.raajneel.Controller;

import com.raajneel.Exception.SubscriptionPlanException;
import com.raajneel.Mapper.SubscriptionPlanMapper;
import com.raajneel.Payload.DTO.SubscriptionPlanDTO;
import com.raajneel.Payload.Response.ApiResponse;
import com.raajneel.Repository.SubscriptionPlanRepository;
import com.raajneel.Service.SubscriptionPlanService;
import com.raajneel.Service.UserService;
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
@RequestMapping("/api/subscription-plans")
@RequiredArgsConstructor
@Tag(name = "Subscription Plans", description = "Endpoints for exploring available membership tiers and administrative management of pricing plans")
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionPlanController {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionPlanMapper subscriptionPlanMapper;
    private final UserService userService;
    private final SubscriptionPlanService subscriptionPlanService;

    @GetMapping
    @Operation(
            summary = "Get all active subscription plans",
            description = "Retrieves all available subscription plans with pricing, loan limits, and borrowing duration allowances."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "List of all active plans returned successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SubscriptionPlanDTO.class)))
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
    public ResponseEntity<List<SubscriptionPlanDTO>> getAllSubscriptionPlans() {
        List<SubscriptionPlanDTO> subscriptionPlans = subscriptionPlanService.getAllSubscriptionPlan();
        return ResponseEntity.ok(subscriptionPlans);
    }

    @GetMapping("/featured")
    @Operation(
            summary = "Get featured subscription plans",
            description = "Retrieves promotional and highlighted plans (isFeatured = true) for display on the landing page."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "List of featured plans returned successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SubscriptionPlanDTO.class)))
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
    public ResponseEntity<List<SubscriptionPlanDTO>> getFeaturedSubscriptionPlans() {
        List<SubscriptionPlanDTO> subscriptionPlans = subscriptionPlanService.getFeaturedSubscriptionPlans();
        return ResponseEntity.ok(subscriptionPlans);
    }

    @PostMapping("/admin/create")
    @Operation(
            summary = "Create a new subscription plan (Admin)",
            description = "Creates a new subscription tier with specified price, duration in days, max books allowed, and loan limits."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Subscription plan created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionPlanDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Plan validation error or duplicate plan code",
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
    public ResponseEntity<SubscriptionPlanDTO> createSubscriptionPlan(
            @RequestBody @Valid SubscriptionPlanDTO subscriptionPlanDTO) throws SubscriptionPlanException, Exception {
        SubscriptionPlanDTO subscriptionPlan = subscriptionPlanService.createSubscriptionPlan(subscriptionPlanDTO);
        return ResponseEntity.ok(subscriptionPlan);
    }

    @PutMapping("/admin/update/{planId}")
    @Operation(
            summary = "Update an existing subscription plan (Admin)",
            description = "Updates pricing, borrowing rules, features, and active status for an existing plan ID."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Subscription plan updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubscriptionPlanDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Plan not found or validation failure",
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
    public ResponseEntity<SubscriptionPlanDTO> updateSubscriptionPlan(
            @Parameter(description = "Identifier of subscription plan to update", example = "2", required = true)
            @PathVariable Long planId,
            @RequestBody @Valid SubscriptionPlanDTO subscriptionPlanDTO) throws SubscriptionPlanException, Exception {
        SubscriptionPlanDTO updatedPlan = subscriptionPlanService.updateSubscriptionPlan(planId, subscriptionPlanDTO);
        return ResponseEntity.ok(updatedPlan);
    }

    @DeleteMapping("/admin/delete/{planId}")
    @Operation(
            summary = "Soft delete / deactivate a subscription plan (Admin)",
            description = "Deactivates a subscription plan (sets isActive = false) so new users cannot purchase it."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Subscription plan successfully deactivated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Plan ID not found",
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
    public ResponseEntity<ApiResponse> deleteSubscriptionPlan(
            @Parameter(description = "Identifier of subscription plan to delete", example = "2", required = true)
            @PathVariable Long planId) throws SubscriptionPlanException, Exception {
        subscriptionPlanService.deleteSubscriptionPlan(planId);
        ApiResponse response = new ApiResponse("Subscription plan deleted successfully", true);
        return ResponseEntity.ok(response);
    }
}
