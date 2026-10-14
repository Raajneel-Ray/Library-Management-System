package com.raajneel.Service;

import com.raajneel.Exception.PaymentException;
import com.raajneel.Exception.UserException;
import com.raajneel.Payload.DTO.PaymentDTO;
import com.raajneel.Payload.Request.PaymentInitiateRequest;
import com.raajneel.Payload.Request.PaymentVerifyRequest;
import com.raajneel.Payload.Response.PaymentInitiateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {

    PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request) throws UserException, PaymentException;

    PaymentDTO verifyPayment(PaymentVerifyRequest request);

    Page<PaymentDTO> getAllPayments(Pageable pageable);

}
