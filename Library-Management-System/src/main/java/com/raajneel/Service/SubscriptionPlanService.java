package com.raajneel.Service;

import com.raajneel.Payload.DTO.SubscriptionPlanDTO;

import java.util.List;

public interface SubscriptionPlanService {

    SubscriptionPlanDTO createSubscriptionPlan(SubscriptionPlanDTO subscriptionPlanDTO);

    SubscriptionPlanDTO updateSubscriptionPlan(Long planId, SubscriptionPlanDTO subscriptionPlanDTO);

    void  deleteSubscriptionPlan(Long planId);

    List<SubscriptionPlanDTO> getAllSubscriptionPlan();

}
