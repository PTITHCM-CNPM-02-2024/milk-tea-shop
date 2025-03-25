package com.mts.backend.domain.customer.repository;

import com.mts.backend.domain.customer.RewardPointLog;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.identifier.RewardPointLogId;

import java.util.List;
import java.util.Optional;

public interface IRewardPointLogRepository {
    Optional<RewardPointLog> findById(RewardPointLogId id);
    RewardPointLog save(RewardPointLog rewardPointLog);
    void delete(RewardPointLog rewardPointLog);
    List<RewardPointLog> findAll();
    List<RewardPointLog> findByCustomerId(CustomerId customerId);
    Optional<RewardPointLog> findByCustomerIdAndOrderId(CustomerId customerId, RewardPointLogId orderId);
}
