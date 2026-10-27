package com.raajneel.Service.Implementation;

import com.raajneel.Domain.FineStatus;
import com.raajneel.Domain.FineType;
import com.raajneel.Domain.PaymentGateway;
import com.raajneel.Domain.PaymentType;
import com.raajneel.Domain.UserRole;
import com.raajneel.Mapper.FineMapper;
import com.raajneel.Model.BookLoan;
import com.raajneel.Model.Fine;
import com.raajneel.Model.User;
import com.raajneel.Payload.DTO.FineDTO;
import com.raajneel.Payload.Request.CreateFineRequest;
import com.raajneel.Payload.Request.PaymentInitiateRequest;
import com.raajneel.Payload.Request.WaiveFineRequest;
import com.raajneel.Payload.Response.PageResponse;
import com.raajneel.Payload.Response.PaymentInitiateResponse;
import com.raajneel.Repository.BookLoanRepository;
import com.raajneel.Repository.FineRepository;
import com.raajneel.Service.FineService;
import com.raajneel.Service.PaymentService;
import com.raajneel.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FineServiceImpl implements FineService {

    private final FineRepository fineRepository;
    private final BookLoanRepository bookLoanRepository;
    private final UserService userService;
    private final FineMapper fineMapper;
    private final PaymentService paymentService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FineDTO createFine(CreateFineRequest createFineRequest) throws Exception {
        // 1. validate book loan exists
        BookLoan bookLoan = bookLoanRepository.findById(createFineRequest.getBookLoanId())
                .orElseThrow(() -> new Exception("Book Loan Not Found with id: " + createFineRequest.getBookLoanId()));

        if (createFineRequest.getAmount() == null || createFineRequest.getAmount() <= 0) {
            throw new IllegalArgumentException("Fine amount must be positive");
        }

        User currentUser = null;
        try {
            currentUser = userService.getCurrentUser();
        } catch (Exception ignored) {
        }

        // 2. create fine
        Fine fine = Fine.builder()
                .bookLoan(bookLoan)
                .user(bookLoan.getUser())
                .fineType(createFineRequest.getType())
                .amount(createFineRequest.getAmount())
                .status(FineStatus.PENDING)
                .reason(createFineRequest.getReason())
                .notes(createFineRequest.getNotes())
                .processedBy(currentUser)
                .build();

        Fine savedFine = fineRepository.save(fine);
        return fineMapper.toDTO(savedFine);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentInitiateResponse payFine(Long fineId, String transactionId) throws Exception {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new Exception("Fine not found with id: " + fineId));

        if (fine.getStatus() == FineStatus.PAID) {
            throw new Exception("Fine is already paid");
        }
        if (fine.getStatus() == FineStatus.WAIVED) {
            throw new Exception("Fine has been waived and cannot be paid");
        }

        // Offline / manual transaction payment
        if (transactionId != null && !transactionId.trim().isEmpty()) {
            fine.applyPayment(fine.getAmount());
            fine.setTransactionId(transactionId.trim());
            try {
                User currentUser = userService.getCurrentUser();
                fine.setProcessedBy(currentUser);
            } catch (Exception ignored) {
            }
            fineRepository.save(fine);

            return PaymentInitiateResponse.builder()
                    .userId(fine.getUser().getId())
                    .transactionId(transactionId.trim())
                    .amount(fine.getAmount())
                    .description("Fine payment - " + fine.getFineType())
                    .success(true)
                    .message("Fine marked as paid successfully")
                    .build();
        }

        // Online payment gateway initiation
        PaymentInitiateRequest initiateRequest = PaymentInitiateRequest.builder()
                .userId(fine.getUser().getId())
                .fineId(fine.getId())
                .bookLoanId(fine.getBookLoan() != null ? fine.getBookLoan().getId() : null)
                .paymentType(PaymentType.FINE)
                .gateway(PaymentGateway.RAZORPAY)
                .amount(fine.getAmount())
                .description("Fine payment for book loan #" + (fine.getBookLoan() != null ? fine.getBookLoan().getId() : "") + " (" + fine.getFineType() + ")")
                .build();

        return paymentService.initiatePayment(initiateRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markFineAsPaid(Long fineId, Long amount, String transactionId) throws Exception {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new Exception("Fine not found with id: " + fineId));

        if (fine.getStatus() == FineStatus.PAID) {
            return;
        }

        fine.applyPayment(amount != null && amount > 0 ? amount : fine.getAmount());
        if (transactionId != null && !transactionId.trim().isEmpty()) {
            fine.setTransactionId(transactionId.trim());
        }
        fineRepository.save(fine);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FineDTO waiveFine(WaiveFineRequest waiveFineRequest) throws Exception {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() != UserRole.ROLE_ADMIN) {
            throw new Exception("Only admins are authorized to waive fines");
        }

        Fine fine = fineRepository.findById(waiveFineRequest.getFineId())
                .orElseThrow(() -> new Exception("Fine not found with id: " + waiveFineRequest.getFineId()));

        if (fine.getStatus() == FineStatus.PAID) {
            throw new Exception("Cannot waive an already paid fine");
        }
        if (fine.getStatus() == FineStatus.WAIVED) {
            throw new Exception("Fine is already waived");
        }

        fine.waive(currentUser, waiveFineRequest.getReason());
        Fine savedFine = fineRepository.save(fine);
        return fineMapper.toDTO(savedFine);
    }

    @Override
    public List<FineDTO> getMyFine(FineStatus status, FineType type) throws Exception {
        User currentUser = userService.getCurrentUser();
        List<Fine> fines = fineRepository.findByUserIdAndFilters(currentUser.getId(), status, type);
        return fines.stream()
                .map(fineMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<FineDTO> getAllFine(FineStatus status, FineType type, Long userId, int page, int size) {
        int pageNumber = Math.max(0, page);
        int pageSize = Math.min(Math.max(1, size), 100);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());

        Page<Fine> finePage = fineRepository.findFinesWithFilter(status, type, userId, pageable);

        List<FineDTO> dtos = finePage.getContent().stream()
                .map(fineMapper::toDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                dtos,
                finePage.getNumber(),
                finePage.getSize(),
                (int) finePage.getTotalElements(),
                finePage.getTotalPages(),
                finePage.isLast(),
                finePage.isFirst(),
                finePage.isEmpty()
        );
    }
}
