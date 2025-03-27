package com.mts.backend.domain.order.jpa;

import com.mts.backend.domain.persistence.entity.OrderTableEntity;
import com.mts.backend.domain.promotion.identifier.CouponId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface JpaOrderTableRepository extends JpaRepository<OrderTableEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO milk_tea_shop_prod.OrderTable (order_table_id, order_id, table_id, check_in, check_out) " +
            "VALUES (:#{#entity.id}, :#{#entity.orderEntity.id}, :#{#entity.table.id}, :#{#entity.checkIn}, :#{#entity.checkOut})",
            nativeQuery = true)
    void insertOrderTable(@Param("entity") OrderTableEntity entity);

    @Modifying
    @Transactional
    @Query(value = "UPDATE milk_tea_shop_prod.OrderTable SET " +
            "order_id = :#{#entity.orderEntity.id}, " +
            "table_id = :#{#entity.table.id}, " +
            "check_in = :#{#entity.checkIn}, " +
            "check_out = :#{#entity.checkOut} " +
            "WHERE order_table_id = :#{#entity.id}",
            nativeQuery = true)
    void updateOrderTable(@Param("entity") OrderTableEntity entity);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.OrderTable WHERE order_table_id = :id",
            nativeQuery = true)
    void deleteOrderTable(@Param("id") Long id);

    @Query(value = "SELECT * FROM milk_tea_shop_prod.OrderTable WHERE order_id = :orderId",
            nativeQuery = true)
    List<OrderTableEntity> findByOrderId(@Param("orderId") CouponId orderId);

    @Query(value = "SELECT * FROM milk_tea_shop_prod.OrderTable WHERE table_id = :tableId",
            nativeQuery = true)
    List<OrderTableEntity> findByTableId(@Param("tableId") Long tableId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.OrderTable WHERE order_id = :orderId",
            nativeQuery = true)
    void deleteByOrderId(@Param("orderId") Long orderId);
}