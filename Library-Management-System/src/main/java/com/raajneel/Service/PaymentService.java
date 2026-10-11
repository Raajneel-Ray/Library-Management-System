package com.raajneel.Service;

import com.raajneel.Payload.DTO.PaymentDTO;
import com.raajneel.Payload.Request.PaymentInitiateRequest;
import com.raajneel.Payload.Request.PaymentVerifyRequest;
import com.raajneel.Payload.Response.PaymentInitiateResponse;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {

    PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request);

    PaymentDTO verifyPayment(PaymentVerifyRequest request);
}
