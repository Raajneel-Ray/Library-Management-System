package com.raajneel.Service;

import com.raajneel.Domain.FineStatus;
import com.raajneel.Domain.FineType;
import com.raajneel.Payload.DTO.FineDTO;
import com.raajneel.Payload.Request.CreateFineRequest;
import com.raajneel.Payload.Request.WaiveFineRequest;
import com.raajneel.Payload.Response.PageResponse;
import com.raajneel.Payload.Response.PaymentInitiateResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FineService {
    FineDTO createFine(CreateFineRequest createFineRequest) throws Exception;
    PaymentInitiateResponse payFine(Long fineId, String transactionId) throws Exception;
    void  markFineAsPaid(Long fineId, Long amount, String transactionId) throws Exception;
    FineDTO waiveFine(WaiveFineRequest waiveFineRequest) throws Exception;
    List<FineDTO> getMyFine(FineStatus status, FineType type) throws Exception;
    PageResponse<FineDTO> getAllFine(FineStatus status, FineType type, Long userId, int page, int size);
}

