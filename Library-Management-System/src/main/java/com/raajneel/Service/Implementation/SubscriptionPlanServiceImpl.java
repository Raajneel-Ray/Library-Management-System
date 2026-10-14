package com.raajneel.Service.Implementation;

import com.raajneel.Domain.UserRole;
import com.raajneel.Exception.SubscriptionPlanException;
import com.raajneel.Mapper.SubscriptionPlanMapper;
import com.raajneel.Model.SubscriptionPlan;
import com.raajneel.Model.User;
import com.raajneel.Payload.DTO.SubscriptionPlanDTO;
import com.raajneel.Repository.SubscriptionPlanRepository;
import com.raajneel.Service.SubscriptionPlanService;
import com.raajneel.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private  final SubscriptionPlanMapper subscriptionPlanMapper;
    private final UserService userService;

    @Override
    public SubscriptionPlanDTO createSubscriptionPlan(SubscriptionPlanDTO subscriptionPlanDTO) throws SubscriptionPlanException, Exception {
        if(subscriptionPlanRepository.existsByPlanCode(subscriptionPlanDTO.getPlanCode())) {
            throw new SubscriptionPlanException("Plan Code already exists");
        }
        SubscriptionPlan  subscriptionPlan = subscriptionPlanMapper.toEntity(subscriptionPlanDTO);

        User currentUser = userService.getCurrentUser();
        subscriptionPlan.setCreatedBy(currentUser.getFullName());
        subscriptionPlan.setUpdatedBy(currentUser.getFullName());

        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(subscriptionPlan);

        return subscriptionPlanMapper.toDTO(savedPlan);
    }

    @Override
    public SubscriptionPlanDTO updateSubscriptionPlan(Long planId, SubscriptionPlanDTO subscriptionPlanDTO) throws SubscriptionPlanException, Exception {
        SubscriptionPlan existingPlan = subscriptionPlanRepository.findById(planId).orElseThrow(
                ()-> new SubscriptionPlanException("Subscription Plan Not Found")
        );

        subscriptionPlanMapper.updateEntityFromDTO(subscriptionPlanDTO, existingPlan);
        User currentUser = userService.getCurrentUser();
        existingPlan.setUpdatedBy(currentUser.getFullName());
        SubscriptionPlan updatedPlan = subscriptionPlanRepository.save(existingPlan);

        return subscriptionPlanMapper.toDTO(updatedPlan);
    }

    @Override
    public void deleteSubscriptionPlan(Long planId) throws SubscriptionPlanException {
        SubscriptionPlan existingPlan = subscriptionPlanRepository.findById(planId).orElseThrow(
                ()-> new SubscriptionPlanException("Subscription Plan Not Found")
        );
        subscriptionPlanRepository.delete(existingPlan);
    }

    @Override
    public List<SubscriptionPlanDTO> getFeaturedSubscriptionPlans() {
        List<SubscriptionPlan> subscriptionPlans = subscriptionPlanRepository.findByIsActiveTrueAndIsFeaturedTrue();
        return subscriptionPlans
                .stream()
                .map(subscriptionPlanMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionPlan getBySubscriptionPlanCode(String subscriptionPlanCode) throws Exception {
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findByPlanCode(subscriptionPlanCode);
        if (subscriptionPlan==null){
            throw new Exception("Plan not found");
        }
        return  subscriptionPlan;
    }

    @Override
    public List<SubscriptionPlanDTO> getAllSubscriptionPlan() {
        User currentUser = null;
        try {
            currentUser = userService.getCurrentUser();
        } catch (Exception ignored) {
        }

        if (currentUser != null && currentUser.getRole() == UserRole.ROLE_ADMIN) {
            List<SubscriptionPlan> subscriptionPlans = subscriptionPlanRepository.findAll();
            return subscriptionPlans
                    .stream()
                    .map(subscriptionPlanMapper::toDTO)
                    .collect(Collectors.toList());
        }

        return getFeaturedSubscriptionPlans();
    }
}
