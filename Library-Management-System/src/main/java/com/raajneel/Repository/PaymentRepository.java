package com.raajneel.Repository;

import com.raajneel.Domain.PaymentStatus;
import com.raajneel.Domain.PaymentType;
import com.raajneel.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findTopByUserIdAndPaymentTypeAndStatusInOrderByCreatedAtDesc(
            Long userId,
            PaymentType paymentType,
            List<PaymentStatus> statuses
    );

    List<Payment> findBySubscriptionId(Long subscriptionId);
}
