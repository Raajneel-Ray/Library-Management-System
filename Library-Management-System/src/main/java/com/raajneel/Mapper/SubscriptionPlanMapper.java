package com.raajneel.Mapper;

import com.raajneel.Model.SubscriptionPlan;
import com.raajneel.Payload.DTO.SubscriptionPlanDTO;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class SubscriptionPlanMapper {
    public SubscriptionPlanDTO toDTO(SubscriptionPlan subscriptionPlan){
        if(subscriptionPlan == null){
            return null;
        }
        return SubscriptionPlanDTO
                .builder()
                .id(subscriptionPlan.getId())
                .planCode(subscriptionPlan.getPlanCode())
                .planName(subscriptionPlan.getName()) // entity.name -> dto.planName
                .description(subscriptionPlan.getDescription())
                .durationDays(subscriptionPlan.getDurationDays())
                .price(subscriptionPlan.getPrice())
                .currency(subscriptionPlan.getCurrency())
                .maximumBooksAllowed(subscriptionPlan.getMaximumBooksAllowed())
                .maxDaysPerBook(subscriptionPlan.getMaxDaysPerBook())
                .displayOrder(subscriptionPlan.getDisplayOrder())
                .isActive(subscriptionPlan.getIsActive())
                .isFeatured(subscriptionPlan.getIsFeatured())
                .badgeText(subscriptionPlan.getBadgeText())
                .adminNotes(subscriptionPlan.getAdminNotes())
                .createdAt(subscriptionPlan.getCreatedDate()) // entity.createdDate -> dto.createdAt
                .updatedAt(subscriptionPlan.getUpdatedDate()) // entity.updatedDate -> dto.updatedAt
                .createdBy(subscriptionPlan.getCreatedBy())
                .updatedBy(subscriptionPlan.getUpdatedBy())
                .build();
    }
    public SubscriptionPlan toEntity(SubscriptionPlanDTO dto) {
        if (dto == null) {
            return null;
        }

        return SubscriptionPlan.builder()
                .id(dto.getId())
                .planCode(dto.getPlanCode())
                .name(dto.getPlanName()) // dto.planName -> entity.name
                .description(dto.getDescription())
                .durationDays(dto.getDurationDays())
                .price(dto.getPrice())
                .currency(dto.getCurrency() != null ? dto.getCurrency() : "INR")
                .maximumBooksAllowed(dto.getMaximumBooksAllowed())
                .maxDaysPerBook(dto.getMaxDaysPerBook())
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .isFeatured(dto.getIsFeatured() != null ? dto.getIsFeatured() : false)
                .badgeText(dto.getBadgeText())
                .adminNotes(dto.getAdminNotes())
                .createdDate(dto.getCreatedAt())
                .updatedDate(dto.getUpdatedAt())
                .createdBy(dto.getCreatedBy())
                .updatedBy(dto.getUpdatedBy())
                .build();
    }
    public void updateEntityFromDTO(SubscriptionPlanDTO dto, SubscriptionPlan entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getPlanCode() != null) entity.setPlanCode(dto.getPlanCode());
        if (dto.getPlanName() != null) entity.setName(dto.getPlanName());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getDurationDays() != null) entity.setDurationDays(dto.getDurationDays());
        if (dto.getPrice() != null) entity.setPrice(dto.getPrice());
        if (dto.getCurrency() != null) entity.setCurrency(dto.getCurrency());
        if (dto.getMaximumBooksAllowed() != null) entity.setMaximumBooksAllowed(dto.getMaximumBooksAllowed());
        if (dto.getMaxDaysPerBook() != null) entity.setMaxDaysPerBook(dto.getMaxDaysPerBook());
        if (dto.getDisplayOrder() != null) entity.setDisplayOrder(dto.getDisplayOrder());
        if (dto.getIsActive() != null) entity.setIsActive(dto.getIsActive());
        if (dto.getIsFeatured() != null) entity.setIsFeatured(dto.getIsFeatured());
        if (dto.getBadgeText() != null) entity.setBadgeText(dto.getBadgeText());
        if (dto.getAdminNotes() != null) entity.setAdminNotes(dto.getAdminNotes());
        if (dto.getUpdatedBy() != null) entity.setUpdatedBy(dto.getUpdatedBy());
    }
}
