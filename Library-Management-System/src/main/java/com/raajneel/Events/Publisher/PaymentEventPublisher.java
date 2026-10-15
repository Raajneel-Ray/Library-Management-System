package com.raajneel.Events.Publisher;

import com.raajneel.Model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;
    public void publishPaymentSuccessEvent(Payment payment){
        applicationEventPublisher.publishEvent(payment);
    }
}
