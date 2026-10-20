package com.raajneel.Service;

import com.raajneel.Domain.BookLoanStatus;
import com.raajneel.Domain.BookLoanType;
import com.raajneel.Payload.DTO.BookLoanDTO;
import com.raajneel.Payload.Request.BookLoanSearchRequest;
import com.raajneel.Payload.Request.CheckinRequest;
import com.raajneel.Payload.Request.CheckoutRequest;
import com.raajneel.Payload.Request.RenewRequest;
import com.raajneel.Payload.Response.PageResponse;
import org.springframework.stereotype.Service;

@Service
public interface BookLoanService {
    BookLoanDTO checkoutBook(CheckoutRequest request) throws Exception;

    BookLoanDTO checkoutBookForUser(Long userId, CheckoutRequest request) throws Exception;

    BookLoanDTO checkinBook(CheckinRequest request);

    BookLoanDTO renewCheckout(RenewRequest renewRequest);

    PageResponse<BookLoanDTO> getMyBookLoans(BookLoanStatus status, int page, int size);

    PageResponse<BookLoanDTO> getBookLoans(BookLoanSearchRequest request);

    int updateOverdueBookLoan();

}


