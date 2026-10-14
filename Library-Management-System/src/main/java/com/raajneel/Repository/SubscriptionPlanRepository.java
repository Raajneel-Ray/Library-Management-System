package com.raajneel.Repository;

import com.raajneel.Model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    Boolean existsByPlanCode(String planCode);

    List<SubscriptionPlan> findByIsActiveTrueAndIsFeaturedTrue();

    List<SubscriptionPlan> findByIsFeaturedTrue();

    SubscriptionPlan findByPlanCode(String planCode);
}
