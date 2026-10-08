package com.raajneel.Mapper;

import com.raajneel.Exception.SubscriptionException;
import com.raajneel.Model.Subscription;
import com.raajneel.Model.SubscriptionPlan;
import com.raajneel.Model.User;
import com.raajneel.Payload.DTO.SubscriptionDTO;
import com.raajneel.Repository.SubscriptionPlanRepository;
import com.raajneel.Repository.UserRepository;
import com.raajneel.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class SubscriptionMapper {
    private final UserRepository userRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserService userService;

    // convert Subscription entity to DTO
    public SubscriptionDTO toDTO(Subscription subscription) {
        if(subscription == null) {
            return null;
        }
        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setId(subscription.getId());

        // user info
        if(subscription.getUser() != null) {
            dto.setUserId(subscription.getUser().getId());
            dto.setUserName(subscription.getUser().getFullName());
            dto.setUserEmail(subscription.getUser().getEmail());
        }

        // plan info
        if(subscription.getPlan() != null) {
            dto.setPlanId(subscription.getPlan().getId());
            dto.setPlanName(subscription.getPlan().getName());
            dto.setPlanCode(subscription.getPlan().getPlanCode());
            dto.setPrice(subscription.getPlan().getPrice());
            dto.setCurrency(subscription.getPlan().getCurrency());
            dto.setMaxBooksAllowed(subscription.getPlan().getMaximumBooksAllowed());
            dto.setMaxDaysPerBook(subscription.getPlan().getMaxDaysPerBook());
        }

        // price in major currency units
        if (dto.getPrice() != null) {
            BigDecimal majorUnitPrice = BigDecimal.valueOf(dto.getPrice())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            dto.setPriceInMajorUnits(majorUnitPrice.toPlainString());
        }
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setIsActive(subscription.getIsActive());
        dto.setAutoRenew(subscription.getAutoRenew());
        dto.setCancelledDate(subscription.getCancelledAt());
        dto.setCancellationReason(subscription.getCancellationReason());
        dto.setNotes(subscription.getNotes());
        dto.setCreatedAt(subscription.getCreatedAt());
        dto.setUpdatedAt(subscription.getUpdatedAt());

        // properties based on current date
        LocalDate today = LocalDate.now();
        LocalDate endDate = subscription.getEndDate();
        LocalDate startDate = subscription.getStartDate();

        if(endDate!= null) {
            long remainingDays = subscription.getDaysRemaining();
            dto.setDaysRemaining(Math.max(0, remainingDays));
            dto.setIsExpired(subscription.isExpired());
        } else {
            dto.setDaysRemaining(null);
            dto.setIsExpired(null);
        }
        dto.setIsValid(subscription.isValid());

        return dto;
    }

    public Subscription toEntity(SubscriptionDTO dto, SubscriptionPlan plan, User user) throws Exception {
        if(dto == null) {
            return null;
        }
        user = userService.getCurrentUser();
//        if(dto.getUserId() != null) {
//            user = userRepository.findById(dto.getUserId())
//                    .orElseThrow(() -> new SubscriptionException("User not found with id " + dto.getUserId()));
//        }


        Subscription subscription = Subscription.builder()
                .id(dto.getId())
                .user(user)
                .plan(plan)
                .notes(dto.getNotes())
                .autoRenew(dto.getAutoRenew() != null ? dto.getAutoRenew() : false)
                .cancelledAt(dto.getCancelledDate())
                .cancellationReason(dto.getCancellationReason())
                .build();

        // Populate plan snapshot fields and calculate end date if plan is provided
        if (subscription.getPlan() != null) {
            subscription.initializeFromPlan();
        }

        // Allow explicit DTO overrides if provided (useful for updates/manual adjustments)
        if (dto.getStartDate() != null) {
            subscription.setStartDate(dto.getStartDate());
            subscription.calculateEndDate();
        }
        if (dto.getEndDate() != null) {
            subscription.setEndDate(dto.getEndDate());
        }
        if (dto.getIsActive() != null) {
            subscription.setIsActive(dto.getIsActive());
        }

        return subscription;
    }

    public List<SubscriptionDTO> toDTOList(List<Subscription> subscriptions) {
        if(subscriptions == null) {
            return null;
        }
        return subscriptions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
