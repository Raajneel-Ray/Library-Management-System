package com.raajneel.Service.Implementation;

import com.raajneel.Domain.PaymentGateway;
import com.raajneel.Domain.PaymentStatus;
import com.raajneel.Events.Publisher.PaymentEventPublisher;
import com.raajneel.Exception.PaymentException;
import com.raajneel.Exception.SubscriptionException;
import com.raajneel.Exception.UserException;
import com.raajneel.Mapper.PaymentMapper;
import com.raajneel.Model.Payment;
import com.raajneel.Model.Subscription;
import com.raajneel.Model.User;
import com.raajneel.Payload.DTO.PaymentDTO;
import com.raajneel.Payload.Request.PaymentInitiateRequest;
import com.raajneel.Payload.Request.PaymentVerifyRequest;
import com.raajneel.Payload.Response.PaymentInitiateResponse;
import com.raajneel.Payload.Response.PaymentLinkResponse;
import com.raajneel.Repository.PaymentRepository;
import com.raajneel.Repository.SubscriptionRepository;
import com.raajneel.Repository.UserRepository;
import com.raajneel.Service.Gateway.RazorpayService;
import com.raajneel.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
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
    private final RazorpayService razorpayService;
    private final PaymentMapper  paymentMapper;
    private  final PaymentEventPublisher paymentEventPublisher;

    @Override
    public PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request) throws UserException, PaymentException {

        User user = userRepository.findById(request.getUserId()).orElseThrow(
                () -> new UserException("User not found with user id : " + request.getUserId())
        );
        /**A new Payment row is inserted into your MySQL database with status PENDING and a generated unique tracking ID.
         * Saving it here generates an ID (payment.getId()), which is needed in the next step.*/
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setPaymentType(request.getPaymentType());
        payment.setGateway(request.getGateway());
        payment.setAmount(request.getAmount());
        payment.setDescription(request.getDescription());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId("TXN_" + UUID.randomUUID());
        payment.setInitiatedAt(LocalDateTime.now());

        if (request.getSubscriptionId() != null) {
            Subscription subscription = subscriptionRepository
                    .findById(request.getSubscriptionId())
                    .orElseThrow(() -> new PaymentException("Subscription not found"));
            payment.setSubscription(subscription);
        }
        payment = paymentRepository.save(payment);

        PaymentInitiateResponse response = new PaymentInitiateResponse();

        //calling the gateway service
        if (request.getGateway() == PaymentGateway.RAZORPAY) {
            PaymentLinkResponse paymentLinkResponse = razorpayService.createPaymentLink(user, payment);

            response = PaymentInitiateResponse.builder()
                    .userId(payment.getUser().getId())
                    .paymentId(payment.getId())
                    .gateway(payment.getGateway())
                    .transactionId(paymentLinkResponse.getPayment_link_id())
                    .amount(payment.getAmount())
                    .description(payment.getDescription())
                    .checkOutUrl(paymentLinkResponse.getPayment_link_url())
                    .success(true)
                    .message("Payment initiated successfully")
                    .build();
            payment.setGatewayOrderId(paymentLinkResponse.getPayment_link_id());
        }
        payment.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);
        // todo payment initiate event
        return response;
    }

    @Override
    public PaymentDTO verifyPayment(PaymentVerifyRequest request) throws Exception {

        JSONObject paymentDetails = razorpayService.fetchPaymentDetails(request.getRazorpayPaymentId());
        JSONObject notes = paymentDetails.getJSONObject("notes");
        Long paymentId = Long.parseLong(notes.optString("payment_id"));
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException("Payment not found"));
        boolean isValid = razorpayService.isValidPayment(request.getRazorpayPaymentId());

        if (PaymentGateway.RAZORPAY==payment.getGateway()){
            if (isValid){
                payment.setGatewayPaymentId(request.getRazorpayPaymentId());
            }
        }
        if (isValid){
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setCompletedAt(LocalDateTime.now());
            payment=paymentRepository.save(payment);

            paymentEventPublisher.publishPaymentSuccessEvent(payment);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Payment failed since its not valid");
            payment=paymentRepository.save(payment);
        }
        return paymentMapper.toDTO(payment);
    }

    @Override
    public Page<PaymentDTO> getAllPayments(Pageable pageable) {
        Page<Payment> payments = paymentRepository.findAll(pageable);
        return payments.map(paymentMapper::toDTO);
    }
}
