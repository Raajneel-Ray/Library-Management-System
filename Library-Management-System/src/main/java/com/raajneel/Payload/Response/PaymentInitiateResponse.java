package com.raajneel.Payload.Response;

import com.raajneel.Domain.PaymentGateway;
import com.raajneel.Payload.Request.PaymentInitiateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInitiateResponse {

    private Long paymentId;

    private PaymentGateway gateway;

    private String transactionId;

    private String razorpayOrderId;

    private Long amount;

    private String description;

    private String checkOutUrl;

    private String message;

    private Boolean success;

}
