package com.raajneel.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "subscriptions") // Explicit table name adheres to standard plural naming conventions
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Native MySQL auto-increment instead of table-based sequence emulation
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY fetch prevents eager N+1 query performance overhead
    @JoinColumn(name = "user_id", nullable = false) // Explicit foreign key column name
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY fetch prevents eager N+1 query performance overhead
    @JoinColumn(name = "plan_id", nullable = false) // Explicit foreign key column name
    private SubscriptionPlan plan; // one plan have many subscriptions

    private String planName;

    private String planCode;

    private Long price;

    @Column(nullable = false)
    private Integer maximumBooksAllowed;

    @Column(nullable = false)
    private Integer maxDaysPerBook;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Builder.Default // Retains default active state (true) when instantiated via Lombok @Builder
    @Column(nullable = false)
    private Boolean isActive = true;

    private Boolean autoRenew;

    private LocalDateTime cancelledAt;

    @Column(length = 500) // Expanded to prevent truncation of cancellation reason text
    private String cancellationReason;

    @Column(length = 500) // Expanded to prevent truncation of subscription notes
    private String notes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false) // updatable = false prevents modification of creation timestamp on entity updates
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public boolean isValid() {
        if (Boolean.FALSE.equals(isActive)) {
            return false;
        }
        if (startDate == null || endDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public boolean isExpired() {
        // Guard against null endDate to prevent NullPointerException
        return endDate != null && LocalDate.now().isAfter(endDate);
    }

    public long getDaysRemaining(){
        // Guard against null endDate or already expired subscription
        if(endDate == null || isExpired()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }

    public void calculateEndDate(){
        // Guard against null durationDays to prevent NullPointerException
        if(plan != null && startDate != null && plan.getDurationDays() != null){
            this.endDate = startDate.plusDays(plan.getDurationDays());
        }
    }

    public void initializeFromPlan(){
        if(plan != null) {
            // 1. Snapshot metadata & pricing
            this.planName = this.plan.getName();
            this.planCode = this.plan.getPlanCode();
            this.price = this.plan.getPrice();

            // 2. Snapshot usage constraints
            this.maximumBooksAllowed = this.plan.getMaximumBooksAllowed();
            this.maxDaysPerBook = this.plan.getMaxDaysPerBook();

            // 3. Set default start date if not already assigned
            if (this.startDate == null) {
                this.startDate = LocalDate.now();
            }

            // 4. Compute end date from plan duration
            calculateEndDate();

            // 5. Initialize active status
            if (this.isActive == null) {
                this.isActive = true;
            }
        }
    }

}
