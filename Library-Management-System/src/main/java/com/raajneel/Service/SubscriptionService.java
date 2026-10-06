package com.raajneel.Service;

import com.raajneel.Payload.DTO.SubscriptionDTO;

import java.awt.print.Pageable;
import java.util.List;

public interface SubscriptionService {

    SubscriptionDTO subscribe(SubscriptionDTO subscriptionDTO) throws Exception;

    SubscriptionDTO getUsersActiveSubscription(Long userId);

    SubscriptionDTO cancelSubscription(Long subscriptionId, String reason);

    SubscriptionDTO activateSubscription(Long subscriptionId, Long paymentId);

    List<SubscriptionDTO> getAllSubscription(Pageable pageable);

}
