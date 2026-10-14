package com.raajneel.Service.Implementation;

import com.raajneel.Domain.PaymentStatus;
import com.raajneel.Exception.PaymentException;
import com.raajneel.Exception.SubscriptionException;
import com.raajneel.Exception.UserException;
import com.raajneel.Model.Payment;
import com.raajneel.Model.Subscription;
import com.raajneel.Model.User;
import com.raajneel.Payload.DTO.PaymentDTO;
import com.raajneel.Payload.Request.PaymentInitiateRequest;
import com.raajneel.Payload.Request.PaymentVerifyRequest;
import com.raajneel.Payload.Response.PaymentInitiateResponse;
import com.raajneel.Repository.PaymentRepository;
import com.raajneel.Repository.SubscriptionRepository;
import com.raajneel.Repository.UserRepository;
import com.raajneel.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request) throws UserException, PaymentException {

        User user = userRepository.findById(request.getUserId()).orElseThrow(
                ()-> new UserException("User not found with user id : " + request.getUserId())
        );
        Payment payment = new  Payment();
        payment.setUser(user);
        payment.setPaymentType(request.getPaymentType());
        payment.setGateway(request.getGateway());
        payment.setAmount(request.getAmount());
        payment.setDescription(request.getDescription());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId("TXN_" + UUID.randomUUID());
        payment.setInitiatedAt(LocalDateTime.now());

        if(request.getSubscriptionId() != null) {
            Subscription subscription = subscriptionRepository
                    .findById(request.getSubscriptionId())
                    .orElseThrow(()-> new PaymentException("Subscription not found"));
            payment.setSubscription(subscription);
        }
        payment = paymentRepository.save(payment);

        // todo
        return null;
    }

    @Override
    public PaymentDTO verifyPayment(PaymentVerifyRequest request) {
        return null;
    }

    @Override
    public Page<PaymentDTO> getAllPayments(Pageable pageable) {
        return null;
    }
}
