package com.raajneel.Controller;

import com.raajneel.Exception.SubscriptionPlanException;
import com.raajneel.Mapper.SubscriptionPlanMapper;
import com.raajneel.Model.SubscriptionPlan;
import com.raajneel.Payload.DTO.SubscriptionPlanDTO;
import com.raajneel.Payload.Response.ApiResponse;
import com.raajneel.Repository.SubscriptionPlanRepository;
import com.raajneel.Service.SubscriptionPlanService;
import com.raajneel.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscription-plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionPlanMapper subscriptionPlanMapper;
    private final UserService userService;
    private final SubscriptionPlanService subscriptionPlanService;

    @GetMapping
    public ResponseEntity<?> getAllSubscriptionPlans() {
        List<SubscriptionPlanDTO> subscriptionPlans = subscriptionPlanService.getAllSubscriptionPlan();
        return ResponseEntity.ok(subscriptionPlans);
    }

    @GetMapping("/featured")
    public ResponseEntity<?> getFeaturedSubscriptionPlans() {
        List<SubscriptionPlanDTO> subscriptionPlans = subscriptionPlanService.getFeaturedSubscriptionPlans();
        return ResponseEntity.ok(subscriptionPlans);
    }

    @PostMapping("/admin/create")
    public ResponseEntity<?> createSubscriptionPlan(
            @RequestBody @Valid SubscriptionPlanDTO subscriptionPlanDTO) throws SubscriptionPlanException, Exception {
        SubscriptionPlanDTO subscriptionPlan = subscriptionPlanService.createSubscriptionPlan(subscriptionPlanDTO);
        return ResponseEntity.ok(subscriptionPlan);
    }

    @PutMapping("/admin/update/{planId}")
    public ResponseEntity<?> updateSubscriptionPlan(@PathVariable Long planId,
            @RequestBody @Valid SubscriptionPlanDTO subscriptionPlanDTO) throws SubscriptionPlanException, Exception {
        SubscriptionPlanDTO updatedPlan = subscriptionPlanService.updateSubscriptionPlan(planId, subscriptionPlanDTO);
        return ResponseEntity.ok(updatedPlan);
    }

    @DeleteMapping("/admin/delete/{planId}")
    public ResponseEntity<?> deleteSubscriptionPlan(
            @PathVariable Long planId) throws SubscriptionPlanException, Exception {
        subscriptionPlanService.deleteSubscriptionPlan(planId);
        ApiResponse response = new ApiResponse("Subscription plan deleted successfully",true);
        return ResponseEntity.ok(response);
    }

}
