package com.raajneel.Service.Implementation;

import com.raajneel.Exception.SubscriptionException;
import com.raajneel.Mapper.SubscriptionMapper;
import com.raajneel.Model.Subscription;
import com.raajneel.Model.SubscriptionPlan;
import com.raajneel.Model.User;
import com.raajneel.Payload.DTO.SubscriptionDTO;
import com.raajneel.Repository.SubscriptionPlanRepository;
import com.raajneel.Repository.SubscriptionRepository;
import com.raajneel.Service.SubscriptionService;
import com.raajneel.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserService userService;

    @Override
    public SubscriptionDTO subscribe(SubscriptionDTO subscriptionDTO) throws Exception {

        // fetch the subscription plan if any
        SubscriptionPlan plan = subscriptionPlanRepository.findById(subscriptionDTO.getId()).orElseThrow(
                ()-> new SubscriptionException("Plan Not found"));

        Subscription subscription = subscriptionMapper.toEntity(subscriptionDTO);
        // subscription.initializeFromPlan();
        Subscription savedSubscription =  subscriptionRepository.save(subscription);

        // create payment todo
        return subscriptionMapper.toDTO(savedSubscription);
    }

    @Override
    public SubscriptionDTO getUsersActiveSubscription(Long userId) {
        return null;
    }

    @Override
    public SubscriptionDTO cancelSubscription(Long subscriptionId, String reason) {
        return null;
    }

    @Override
    public SubscriptionDTO activateSubscription(Long subscriptionId, Long paymentId) {
        return null;
    }

    @Override
    public List<SubscriptionDTO> getAllSubscription(Pageable pageable) {
        return List.of();
    }
}
