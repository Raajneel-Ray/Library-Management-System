package com.raajneel.Events.Listner;

import com.raajneel.Model.Payment;
import com.raajneel.Service.FineService;
import com.raajneel.Service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final SubscriptionService subscriptionService;
    private final FineService fineService;

    @Async
    @EventListener
    @Transactional
    public void handlePaymentSuccess(Payment payment) throws Exception {
        switch (payment.getPaymentType()){
            case FINE :
            case LOST_BOOK_PENALTY:
            case DAMAGED_BOOK_PENALTY:
                if (payment.getFine() != null) {
                    fineService.markFineAsPaid(payment.getFine().getId(), payment.getAmount(), payment.getTransactionId());
                }
                break;
            case MEMBERSHIP:
                if (payment.getSubscription() != null) {
                    subscriptionService.activateSubscription(payment.getSubscription().getId(), payment.getId());
                }
                break;
            default:
                break;
        }
    }
}
