package com.raajneel.Repository;

import com.raajneel.Model.SubscriptionPlan;
import com.raajneel.Payload.DTO.SubscriptionPlanDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    Boolean existsByPlanCode(String planCode);
}
