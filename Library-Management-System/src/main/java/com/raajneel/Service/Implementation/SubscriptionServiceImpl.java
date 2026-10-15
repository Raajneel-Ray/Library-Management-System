package com.raajneel.Service.Implementation;

import com.raajneel.Domain.PaymentGateway;
import com.raajneel.Domain.PaymentStatus;
import com.raajneel.Domain.PaymentType;
import com.raajneel.Domain.UserRole;
import com.raajneel.Exception.SubscriptionException;
import com.raajneel.Exception.UserException;
import com.raajneel.Mapper.SubscriptionMapper;
import com.raajneel.Model.Payment;
import com.raajneel.Model.Subscription;
import com.raajneel.Model.SubscriptionPlan;
import com.raajneel.Model.User;
import com.raajneel.Payload.DTO.SubscriptionDTO;
import com.raajneel.Payload.Request.PaymentInitiateRequest;
import com.raajneel.Payload.Response.PaymentInitiateResponse;
import com.raajneel.Repository.PaymentRepository;
import com.raajneel.Repository.SubscriptionPlanRepository;
import com.raajneel.Repository.SubscriptionRepository;
import com.raajneel.Repository.UserRepository;
import com.raajneel.Service.PaymentService;
import com.raajneel.Service.SubscriptionService;
import com.raajneel.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentInitiateResponse subscribe(SubscriptionDTO subscriptionDTO) throws Exception {
        User user = userService.getCurrentUser();
        SubscriptionPlan plan = subscriptionPlanRepository.findById(subscriptionDTO.getPlanId())
                .orElseThrow(() -> new SubscriptionException("Plan not found with ID: " + subscriptionDTO.getPlanId()));
        if (!Boolean.TRUE.equals(plan.getIsFeatured())) {
            throw new SubscriptionException("Subscription plan is not accessible for subscription until it is featured");
        }

        if (!Boolean.TRUE.equals(plan.getIsActive())) {
            throw new SubscriptionException("Subscription plan is not active");
        }

        // 1. Prevent duplicate request: check if user already has an active subscription
        Optional<Subscription> activeSubscription = subscriptionRepository
                .findActiveSubscriptionByUserId(user.getId(), LocalDate.now());
        if (activeSubscription.isPresent()) {
            throw new SubscriptionException("User already has an active subscription to plan: "
                    + activeSubscription.get().getPlanName() + ". Duplicate subscription requests are not allowed.");
        }

        // 2. Prevent duplicate request: check if user has an in-progress / pending payment for membership
        List<PaymentStatus> pendingStatuses = List.of(PaymentStatus.PENDING, PaymentStatus.PROCESSING);
        Optional<Payment> recentPendingPayment = paymentRepository
                .findTopByUserIdAndPaymentTypeAndStatusInOrderByCreatedAtDesc(user.getId(), PaymentType.MEMBERSHIP, pendingStatuses);

        if (recentPendingPayment.isPresent()) {
            Payment pendingPayment = recentPendingPayment.get();
            LocalDateTime cutoff = LocalDateTime.now().minusMinutes(15);
            if (pendingPayment.getCreatedAt() != null && pendingPayment.getCreatedAt().isAfter(cutoff)) {
                throw new SubscriptionException("A subscription request is already in progress. Please complete the pending payment or wait 15 minutes before trying again.");
            } else {
                pendingPayment.setStatus(PaymentStatus.FAILED);
                pendingPayment.setFailureReason("Pending payment session expired");
                paymentRepository.save(pendingPayment);
            }
        }

        // 3. Mark any older uncompleted inactive subscriptions as superseded to prevent accumulation
        List<Subscription> staleSubscriptions = subscriptionRepository.findPendingSubscriptionsByUserId(user.getId());
        for (Subscription stale : staleSubscriptions) {
            stale.setCancelledAt(LocalDateTime.now());
            stale.setCancellationReason("Superseded by new subscription request");
            subscriptionRepository.save(stale);
        }

        Subscription subscription = subscriptionMapper.toEntity(subscriptionDTO, plan, user);
        subscription.setIsActive(false); // only activate after payment is up
        Subscription savedSubscription = subscriptionRepository.save(subscription);

        PaymentInitiateRequest paymentInitiateRequest = PaymentInitiateRequest.builder()
                .userId(user.getId())
                .subscriptionId(savedSubscription.getId())
                .paymentType(PaymentType.MEMBERSHIP)
                .gateway(PaymentGateway.RAZORPAY)
                .amount(savedSubscription.getPrice())
                .description("Library Subscription -" + plan.getName())
                .build();
        return paymentService.initiatePayment(paymentInitiateRequest);
    }

    @Override
    public SubscriptionDTO getUsersActiveSubscription(Long userId) throws Exception {
        User currentUser = userService.getCurrentUser();
        Long targetUserId = (userId != null) ? userId : currentUser.getId();

        boolean isSelf = targetUserId.equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == UserRole.ROLE_ADMIN;

        if (!isSelf && !isAdmin) {
            throw new SubscriptionException("You do not have permission to view this subscription");
        }

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserException("User not found"));
        Subscription subscription = subscriptionRepository
                .findActiveSubscriptionByUserId(user.getId(), LocalDate.now())
                .orElseThrow(() -> new SubscriptionException("Active Subscription Not Found"));
        return subscriptionMapper.toDTO(subscription);
    }

    @Override
    public SubscriptionDTO cancelSubscription(Long subscriptionId, String reason) throws Exception {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionException("Subscription Not found with id : " + subscriptionId));

        User currentUser = userService.getCurrentUser();
        boolean isOwner = subscription.getUser() != null && subscription.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == UserRole.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new SubscriptionException("You do not have permission to cancel this subscription");
        }

        if (!subscription.getIsActive()) {
            throw new SubscriptionException("Subscription Not Active");
        }

        // mark as canceled
        subscription.setIsActive(false);
        subscription.setCancelledAt(LocalDateTime.now());
        subscription.setCancellationReason(reason != null ? reason : (isAdmin && !isOwner ? "Cancelled By Admin" : "Cancelled By User"));
        subscription = subscriptionRepository.save(subscription);

        return subscriptionMapper.toDTO(subscription);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubscriptionDTO activateSubscription(Long subscriptionId, Long paymentId) throws SubscriptionException {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionException("Subscription Not found with id : " + subscriptionId));

        if (Boolean.TRUE.equals(subscription.getIsActive())) {
            return subscriptionMapper.toDTO(subscription);
        }

        subscription.setIsActive(true);
        subscription.setStartDate(LocalDate.now());
        if (subscription.getPlan() != null && subscription.getPlan().getDurationDays() != null) {
            subscription.setEndDate(LocalDate.now().plusDays(subscription.getPlan().getDurationDays()));
        }
        subscription = subscriptionRepository.save(subscription);
        return subscriptionMapper.toDTO(subscription);
    }

    @Override
    public List<SubscriptionDTO> getAllSubscription(Pageable pageable) {
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        return subscriptionMapper.toDTOList(subscriptions);
    }

    @Override
    public void deactivateExpiredSubscription() {
        List<Subscription> subscriptions = subscriptionRepository.findExpiredActiveSubscription(LocalDate.now());
        for(Subscription subscription : subscriptions){
            subscription.setIsActive(false);
            subscriptionRepository.save(subscription);
        }
    }
}
