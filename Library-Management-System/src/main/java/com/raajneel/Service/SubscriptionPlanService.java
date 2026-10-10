package com.raajneel.Service;

import com.raajneel.Exception.SubscriptionPlanException;
import com.raajneel.Payload.DTO.SubscriptionPlanDTO;

import java.util.List;

public interface SubscriptionPlanService {

    SubscriptionPlanDTO createSubscriptionPlan(SubscriptionPlanDTO subscriptionPlanDTO) throws SubscriptionPlanException, Exception;

    SubscriptionPlanDTO updateSubscriptionPlan(Long planId, SubscriptionPlanDTO subscriptionPlanDTO) throws SubscriptionPlanException, Exception;

    void  deleteSubscriptionPlan(Long planId) throws SubscriptionPlanException;

    List<SubscriptionPlanDTO> getAllSubscriptionPlan();

    List<SubscriptionPlanDTO> getFeaturedSubscriptionPlans();
}
