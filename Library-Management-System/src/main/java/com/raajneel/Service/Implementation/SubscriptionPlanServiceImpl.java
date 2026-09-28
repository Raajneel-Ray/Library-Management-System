package com.raajneel.Service.Implementation;

import com.raajneel.Payload.DTO.SubscriptionPlanDTO;
import com.raajneel.Service.SubscriptionPlanService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {
    @Override
    public SubscriptionPlanDTO createSubscriptionPlan(SubscriptionPlanDTO subscriptionPlanDTO) {
        return null;
    }

    @Override
    public SubscriptionPlanDTO updateSubscriptionPlan(Long planId, SubscriptionPlanDTO subscriptionPlanDTO) {
        return null;
    }

    @Override
    public void deleteSubscriptionPlan(Long planId) {

    }

    @Override
    public List<SubscriptionPlanDTO> getAllSubscriptionPlan() {
        return List.of();
    }
}
