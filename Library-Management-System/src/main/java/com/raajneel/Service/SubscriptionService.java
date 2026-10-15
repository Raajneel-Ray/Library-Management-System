package com.raajneel.Service;

import com.raajneel.Exception.SubscriptionException;
import com.raajneel.Payload.DTO.SubscriptionDTO;
import com.raajneel.Payload.Response.PaymentInitiateResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubscriptionService {

    PaymentInitiateResponse subscribe(SubscriptionDTO subscriptionDTO) throws Exception;

    SubscriptionDTO getUsersActiveSubscription(Long userId) throws Exception;

    SubscriptionDTO cancelSubscription(Long subscriptionId, String reason) throws Exception;

    SubscriptionDTO activateSubscription(Long subscriptionId, Long paymentId) throws SubscriptionException;

    List<SubscriptionDTO> getAllSubscription(Pageable pageable);

    void deactivateExpiredSubscription();
}
