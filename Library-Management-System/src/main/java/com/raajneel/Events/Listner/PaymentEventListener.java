package com.raajneel.Events.Listner;

import com.raajneel.Exception.SubscriptionException;
import com.raajneel.Model.Payment;
import com.raajneel.Service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.raajneel.Domain.PaymentType.MEMBERSHIP;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final SubscriptionService subscriptionService;

    @Async
    @EventListener
    @Transactional
    public void handlePaymentSuccess(Payment payment) throws SubscriptionException {
        switch (payment.getPaymentType()){
            case FINE :
            case LOST_BOOK_PENALTY:
            case DAMAGED_BOOK_PENALTY:
                break;
            case MEMBERSHIP:
                subscriptionService.activateSubscription(payment.getSubscription().getId(), payment.getId());
        }
    }
}
