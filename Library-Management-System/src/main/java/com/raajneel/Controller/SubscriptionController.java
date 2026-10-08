package com.raajneel.Controller;

import com.raajneel.Exception.SubscriptionException;
import com.raajneel.Payload.DTO.SubscriptionDTO;
import com.raajneel.Payload.Response.ApiResponse;
import com.raajneel.Service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(
            @Valid @RequestBody SubscriptionDTO subscriptionDTO
    ) throws Exception {
        SubscriptionDTO dto = subscriptionService.subscribe(subscriptionDTO);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping("/admin")
    public ResponseEntity<?> getAllSubscriptions() {
        Pageable pageable = (Pageable) PageRequest.of(0, 10);
        List<SubscriptionDTO> list = subscriptionService.getAllSubscription(pageable);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/user/active-subscriptions")
    public ResponseEntity<?> getUsersActiveSubscription(@RequestParam Long userId) throws Exception {
        SubscriptionDTO dto = subscriptionService.getUsersActiveSubscription(userId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/admin/deactivate-expired")
    public ResponseEntity<?> deactivateExpiredSubscriptions() {
        subscriptionService.deactivateExpiredSubscription();
        ApiResponse response = new ApiResponse("Task done successfully!", true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel/{subscriptionId}")
    public ResponseEntity<?> cancelSubscription(
            @PathVariable Long subscriptionId, @RequestParam(required = false) String reason) throws Exception {
        SubscriptionDTO dto = subscriptionService.cancelSubscription(subscriptionId, reason);
        return ResponseEntity.ok(dto);

    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateSubscription(
            @RequestParam Long subscriptionId,
            @RequestParam Long paymentId) throws Exception {
        SubscriptionDTO dto = subscriptionService.activateSubscription(subscriptionId, paymentId);
        return ResponseEntity.ok(dto);

    }

}
