package com.mts.backend.infrastructure.order.jpa;

import com.mts.backend.infrastructure.persistence.entity.OrderProductEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface JpaOrderProductRepository extends JpaRepository<OrderProductEntity, Long> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO milk_tea_shop_prod.OrderProduct (order_product_id, order_id, product_price_id, " +
            "quantity, `option`)" +
            "VALUES (:#{#entity.id},:#{#entity.orderEntity.id}, :#{#entity.productPriceEntity.id}, :#{#entity.quantity}, :#{#entity.option})",
            nativeQuery = true)
    void insertOrderProduct(@Param("entity") OrderProductEntity entity);


    @Transactional
    @Modifying
    @Query(value = "UPDATE milk_tea_shop_prod.OrderProduct SET " +
            "order_id = :#{#entity.orderEntity.id}, " +
            "product_price_id = :#{#entity.productPriceEntity.id}, " +
            "quantity = :#{#entity.quantity}, " +
            "`option` = :#{#entity.option} " +
            "WHERE order_product_id = :#{#entity.id}",
            nativeQuery = true)
    void updateOrderProduct(@Param("entity") OrderProductEntity entity);


    @Transactional
    @Modifying
    @Query(value = "DELETE FROM milk_tea_shop_prod.OrderProduct WHERE order_product_id = :id",
            nativeQuery = true)
    void deleteOrderProduct(@Param("id") Long id);

    @Query("select o from OrderProductEntity o where o.orderEntity.id = :id and o.productPriceEntity.id = :id1")
    Optional<OrderProductEntity> findByOrderEntity_IdAndProductPriceEntity_Id(@Param("id") @NonNull Long id, @Param("id1") @NonNull Long id1);

    @Query("select o from OrderProductEntity o where o.orderEntity.id = :id")
    List<OrderProductEntity> findByOrderEntity_Id(@Param("id") @NonNull Long id);


}