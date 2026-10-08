package com.raajneel.Service.Implementation;

import com.raajneel.Exception.SubscriptionException;
import com.raajneel.Exception.UserException;
import com.raajneel.Mapper.SubscriptionMapper;
import com.raajneel.Model.Subscription;
import com.raajneel.Model.SubscriptionPlan;
import com.raajneel.Model.User;
import com.raajneel.Payload.DTO.SubscriptionDTO;
import com.raajneel.Repository.SubscriptionPlanRepository;
import com.raajneel.Repository.SubscriptionRepository;
import com.raajneel.Repository.UserRepository;
import com.raajneel.Service.SubscriptionService;
import com.raajneel.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public SubscriptionDTO subscribe(SubscriptionDTO subscriptionDTO) throws Exception {
        User user = userService.getCurrentUser();
        SubscriptionPlan plan = subscriptionPlanRepository.findById(subscriptionDTO.getPlanId())
                .orElseThrow(() -> new SubscriptionException("Plan not found with ID: " + subscriptionDTO.getPlanId()));
        Subscription subscription = subscriptionMapper.toEntity(subscriptionDTO, plan, user);
        subscription.setIsActive(false); // only activate after payment is up
        Subscription savedSubscription =  subscriptionRepository.save(subscription);

        // create payment todo
        // todo prevention of duplicate subscription request
        // todo subscribe() does not verify whether plan.getIsActive() is true.
        //  Archived or disabled plans can still be purchased.
        return subscriptionMapper.toDTO(savedSubscription);
    }

    @Override
    public SubscriptionDTO getUsersActiveSubscription(Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));
        Subscription subscription = subscriptionRepository
                .findActiveSubscriptionByUserId(user.getId(), LocalDate.now())
                .orElseThrow(()-> new SubscriptionException("Active Subscription Not Found"));
        return subscriptionMapper.toDTO(subscription);
    }

    @Override
    public SubscriptionDTO cancelSubscription(Long subscriptionId, String reason) throws SubscriptionException {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(()->new SubscriptionException("Subscription Not found with id : " + subscriptionId ));

        if(!subscription.getIsActive()) {
            throw new SubscriptionException("Subscription Not Active");
        }

        // mark as canceled
        subscription.setIsActive(false);
        subscription.setCancelledAt(LocalDateTime.now());
        subscription.setCancellationReason(reason!=null ? reason : "Cancelled By User");
        subscription = subscriptionRepository.save(subscription);

        return subscriptionMapper.toDTO(subscription);
    }

    @Override
    public SubscriptionDTO activateSubscription(Long subscriptionId, Long paymentId) throws SubscriptionException {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(()->new SubscriptionException("Subscription Not found with id : " + subscriptionId ));

        // verify payment todo
        subscription.setIsActive(true);
        subscription =  subscriptionRepository.save(subscription);
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
