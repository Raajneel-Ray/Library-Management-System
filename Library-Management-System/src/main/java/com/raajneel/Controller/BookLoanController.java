package com.raajneel.Controller;

import com.raajneel.Domain.BookLoanStatus;
import com.raajneel.Payload.DTO.BookLoanDTO;
import com.raajneel.Payload.Request.BookLoanSearchRequest;
import com.raajneel.Payload.Request.CheckinRequest;
import com.raajneel.Payload.Request.CheckoutRequest;
import com.raajneel.Payload.Request.RenewRequest;
import com.raajneel.Payload.Response.ApiResponse;
import com.raajneel.Payload.Response.PageResponse;
import com.raajneel.Service.BookLoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-loans")
public class BookLoanController {
    private final BookLoanService bookLoanService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkoutBook(@Valid @RequestBody CheckoutRequest checkoutRequest) throws Exception {
        BookLoanDTO bookLoan=bookLoanService.checkoutBook(checkoutRequest);
        return  new ResponseEntity<>(bookLoan, HttpStatus.CREATED);
    }

    @PostMapping("/checkout/user/{userId}")
    public ResponseEntity<?> checkoutBookForUser(@PathVariable Long userId, @Valid @RequestBody CheckoutRequest checkoutRequest)throws Exception{
        BookLoanDTO bookLoan=bookLoanService.checkoutBookForUser(userId,checkoutRequest);
        return  new ResponseEntity<>(bookLoan,HttpStatus.CREATED);
    }

    @PostMapping("/checkin")
    public ResponseEntity<?> checkin(@Valid @RequestBody CheckinRequest checkinRequest) throws Exception {
        BookLoanDTO bookLoan=bookLoanService.checkinBook(checkinRequest);
        return  new ResponseEntity<>(bookLoan,HttpStatus.OK);
    }

    @PostMapping("/renew")
    public ResponseEntity<?> renew(@Valid @RequestBody RenewRequest renewalRequest) throws  Exception{
        BookLoanDTO bookLoan=bookLoanService.renewCheckout(renewalRequest);
        return  new ResponseEntity<>(bookLoan,HttpStatus.OK);
    }

    @GetMapping("/mybookloans")
    public  ResponseEntity<?> getMyBookLoans(
            @RequestParam(required = false) BookLoanStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) throws  Exception{
        PageResponse<BookLoanDTO> bookLoans=bookLoanService.getMyBookLoans(status, page, size);
        return ResponseEntity.ok(bookLoans);
    }

    @PostMapping("/search")
    public ResponseEntity<?> getAllBookLoans(@RequestBody BookLoanSearchRequest searchRequest){
        PageResponse<BookLoanDTO> bookLoans=bookLoanService.getBookLoans(searchRequest);
        return ResponseEntity.ok(bookLoans);
    }

    @PostMapping("/admin/update-overdue")
    public ResponseEntity<?> updateOverdueBookLoans(){
        int updateCount=bookLoanService.updateOverdueBookLoan();
        return  ResponseEntity.ok(new ApiResponse("Overdue book loans are updated",true));
    }
}
