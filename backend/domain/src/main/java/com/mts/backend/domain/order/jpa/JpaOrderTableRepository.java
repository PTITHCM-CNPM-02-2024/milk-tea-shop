package com.mts.backend.domain.order.jpa;

import com.mts.backend.domain.order.OrderTableEntity;
import com.mts.backend.domain.promotion.identifier.CouponId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface JpaOrderTableRepository extends JpaRepository<OrderTableEntity, Long> {
    

    @Query(value = "SELECT * FROM milk_tea_shop_prod.OrderTable WHERE table_id = :tableId",
            nativeQuery = true)
    List<OrderTableEntity> findByTableId(@Param("tableId") Long tableId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.OrderTable WHERE order_id = :orderId",
            nativeQuery = true)
    void deleteByOrderId(@Param("orderId") Long orderId);
}