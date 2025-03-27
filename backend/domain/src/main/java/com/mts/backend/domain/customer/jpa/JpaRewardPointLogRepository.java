package com.mts.backend.domain.customer.jpa;

import com.mts.backend.domain.persistence.entity.RewardPointLogEntity;
import com.mts.backend.domain.promotion.identifier.CouponId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface JpaRewardPointLogRepository extends JpaRepository<RewardPointLogEntity, Long> {
    
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO milk_tea_shop_prod.RewardPointLog (reward_point_log_id,customer_id,order_id, " +
            "reward_point) " +
        "VALUES (:#{#rewardPointLog.id},  :#{#rewardPointLog.customerEntity.id}, :#{#rewardPointLog.order.id},:#{#rewardPointLog.rewardPoint})",
            nativeQuery = true)
    void insertRewardPointLog(@Param("rewardPointLog") RewardPointLogEntity rewardPointLog);
    
    @Transactional
    @Modifying
    @Query(value = "UPDATE milk_tea_shop_prod.RewardPointLog SET " +
            "customer_id = :#{#rewardPointLog.customerEntity.id}, " +
            "order_id = :#{#rewardPointLog.order.id}, " +
            "reward_point = :#{#rewardPointLog.rewardPoint} " +
            "WHERE reward_point_log_id = :#{#rewardPointLog.id}",
            nativeQuery = true)
    void updateRewardPointLog(@Param("rewardPointLog") RewardPointLogEntity rewardPointLog);
    
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM milk_tea_shop_prod.RewardPointLog WHERE reward_point_log_id = :id",
            nativeQuery = true)
    
    void deleteRewardPointLog(@Param("id") CouponId id);

    @Query("select r from RewardPointLogEntity r where r.customerEntity.id = :id order by r.createdAt DESC")
    List<RewardPointLogEntity> findByCustomerEntity_IdOrderByCreatedAtDesc(@Param("id") @NonNull CouponId id);

    @Query("select r from RewardPointLogEntity r where r.customerEntity.id = :id and r.order.id = :id1")
    Optional<RewardPointLogEntity> findByCustomerEntity_IdAndOrder_Id(@Param("id") CouponId id, @Param("id1") CouponId id1);


}