package com.mts.backend.infrastructure.order.jpa;

import com.mts.backend.infrastructure.persistence.entity.OrderDiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface JpaOrderDiscountRepository extends JpaRepository<OrderDiscountEntity, Long> {
  @Modifying
  @Transactional
  @Query(value = "INSERT INTO milk_tea_shop_prod.OrderDiscount (order_discount_id, order_id, discount_id, " +
          "discount_amount)" +
          "VALUES (:#{#entity.id},:#{#entity.orderEntity.id}, :#{#entity.discount.id}, :#{#entity.discountAmount})",
          nativeQuery = true)
  void insertOrderDiscount(@Param("entity") OrderDiscountEntity entity);
  
  @Modifying
    @Transactional
    @Query(value = "UPDATE milk_tea_shop_prod.OrderDiscount SET " +
            "order_id = :#{#entity.orderEntity.id}, " +
            "discount_id = :#{#entity.discount.id}, " +
            "discount_amount = :#{#entity.discountAmount} " +
            "WHERE order_discount_id = :#{#entity.id}",
            nativeQuery = true)
    void updateOrderDiscount(@Param("entity") OrderDiscountEntity entity);

  @Query("select (count(o) > 0) from OrderDiscountEntity o where o.orderEntity.id = :id")
  boolean existsByOrder_Id(@Param("id") Long id);

  @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.OrderDiscount WHERE order_discount_id = :id",
            nativeQuery = true)
  
    void deleteOrderDiscount(@Param("id") Long id);

  @Query("select o from OrderDiscountEntity o where o.orderEntity.id = :id")
  List<OrderDiscountEntity> findByOrder_Id(@Param("id") Long id);

  @Query("select o from OrderDiscountEntity o where o.orderEntity.id = :id and o.discount.id = :id1")
  Optional<OrderDiscountEntity> findByOrder_IdAndDiscount_Id(@Param("id") Long id, @Param("id1") Long id1);

  @Query("select (count(o) > 0) from OrderDiscountEntity o where o.discount.id = :id")
  boolean existsByDiscount_Id(@Param("id") @NonNull Long id);


}