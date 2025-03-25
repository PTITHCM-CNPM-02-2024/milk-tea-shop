package com.mts.backend.infrastructure.customer.repository;

import com.mts.backend.domain.customer.RewardPointLog;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.identifier.RewardPointLogId;
import com.mts.backend.domain.customer.repository.IRewardPointLogRepository;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.infrastructure.customer.jpa.JpaRewardPointLogRepository;
import com.mts.backend.infrastructure.persistence.entity.CustomerEntity;
import com.mts.backend.infrastructure.persistence.entity.OrderEntity;
import com.mts.backend.infrastructure.persistence.entity.RewardPointLogEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RewardPointLogRepository implements IRewardPointLogRepository {
    private final JpaRewardPointLogRepository rewardPointLogRepository;
        
    
    public RewardPointLogRepository(JpaRewardPointLogRepository rewardPointLogRepository) {
        this.rewardPointLogRepository = rewardPointLogRepository;
    }
    /**
     * @param id 
     * @return
     */
    @Override
    public Optional<RewardPointLog> findById(RewardPointLogId id) {
        Objects.requireNonNull(id, "RewardPointLog id is required");
        
        return rewardPointLogRepository.findById(id.getValue()).map(e -> new RewardPointLog(
                RewardPointLogId.of(e.getId()),
                OrderId.of(e.getOrder().getId()),
                CustomerId.of(e.getCustomerEntity().getId()),
                e.getRewardPoint(),
                e.getUpdatedAt().orElse(LocalDateTime.now())
        ));
    }

    /**
     * @param rewardPointLog 
     * @return
     */
    @Override
    @Transactional
    public RewardPointLog save(RewardPointLog rewardPointLog) {
        Objects.requireNonNull(rewardPointLog, "RewardPointLog is required");
        
        try {
            if (rewardPointLogRepository.existsById(rewardPointLog.getId().getValue())) {
                return update(rewardPointLog);
            } else {
                return create(rewardPointLog);
            }
        } catch (Exception e) {
            throw new RuntimeException("Không thể lưu điểm thưởng", e);
        }
    }
    
    
    @Transactional
    protected RewardPointLog create(RewardPointLog rewardPointLog) {
        Objects.requireNonNull(rewardPointLog, "RewardPointLog is required");
        
        var en = RewardPointLogEntity.builder()
                .id(rewardPointLog.getId().getValue())
                .rewardPoint(rewardPointLog.getPoint()).build();
        
        var cusEn = CustomerEntity.builder().id(rewardPointLog.getCustomerId().getValue()).build();
        var orderEn = OrderEntity.builder().id(rewardPointLog.getOrderId().getValue()).build();
        
        en.setCustomerEntity(cusEn);
        
        en.setOrder(orderEn);
        
        rewardPointLogRepository.insertRewardPointLog(en);
        
        return rewardPointLog;
    }
    
    @Transactional
    protected RewardPointLog update(RewardPointLog rewardPointLog) {
        Objects.requireNonNull(rewardPointLog, "RewardPointLog is required");
        
        var en = RewardPointLogEntity.builder()
                .id(rewardPointLog.getId().getValue())
                .rewardPoint(rewardPointLog.getPoint()).build();
        
        var cusEn = CustomerEntity.builder().id(rewardPointLog.getCustomerId().getValue()).build();
        var orderEn = OrderEntity.builder().id(rewardPointLog.getOrderId().getValue()).build();
        
        en.setCustomerEntity(cusEn);
        en.setOrder(orderEn);
        
        rewardPointLogRepository.updateRewardPointLog(en);
        
        return rewardPointLog;
    }

    /**
     * @param rewardPointLog 
     */
    @Override
    public void delete(RewardPointLog rewardPointLog) {
        Objects.requireNonNull(rewardPointLog, "RewardPointLog is required");
        
        rewardPointLogRepository.deleteRewardPointLog(rewardPointLog.getId().getValue());
    }

    /**
     * @return 
     */
    @Override
    public List<RewardPointLog> findAll() {
        
        return rewardPointLogRepository.findAll().stream()
                .map(e -> new RewardPointLog(
                        RewardPointLogId.of(e.getId()),
                        OrderId.of(e.getOrder().getId()),
                        CustomerId.of(e.getCustomerEntity().getId()),
                        e.getRewardPoint(),
                        e.getUpdatedAt().orElse(LocalDateTime.now())
                )).toList();
    }

    /**
     * @param customerId 
     * @return
     */
    @Override
    public List<RewardPointLog> findByCustomerId(CustomerId customerId) {
        Objects.requireNonNull(customerId, "Customer id is required");
        
        return rewardPointLogRepository.findByCustomerEntity_IdOrderByCreatedAtDesc(customerId.getValue()).stream()
                .map(e -> new RewardPointLog(
                        RewardPointLogId.of(e.getId()),
                        OrderId.of(e.getOrder().getId()),
                        CustomerId.of(e.getCustomerEntity().getId()),
                        e.getRewardPoint(),
                        e.getUpdatedAt().orElse(LocalDateTime.now())
                )).toList();
    }

    /**
     * @param customerId 
     * @param orderId
     * @return
     */
    @Override
    public Optional<RewardPointLog> findByCustomerIdAndOrderId(CustomerId customerId, RewardPointLogId orderId) {
        
        Objects.requireNonNull(customerId, "Customer id is required");
        
        Objects.requireNonNull(orderId, "Order id is required");
        
        return rewardPointLogRepository.findByCustomerEntity_IdAndOrder_Id(customerId.getValue(), orderId.getValue())
                .map(e -> new RewardPointLog(
                        RewardPointLogId.of(e.getId()),
                        OrderId.of(e.getOrder().getId()),
                        CustomerId.of(e.getCustomerEntity().getId()),
                        e.getRewardPoint(),
                        e.getUpdatedAt().orElse(LocalDateTime.now())
                ));
    }
}
