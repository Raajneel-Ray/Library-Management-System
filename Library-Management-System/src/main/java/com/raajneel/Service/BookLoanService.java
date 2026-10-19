package com.raajneel.Service;

import com.raajneel.Payload.DTO.BookLoanDTO;
import com.raajneel.Payload.Request.CheckoutRequest;
import org.springframework.stereotype.Service;

@Service
public interface BookLoanService {
    BookLoanDTO checkoutBook(CheckoutRequest request);

}


