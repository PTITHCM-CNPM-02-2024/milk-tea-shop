package com.mts.backend.domain.order.jpa;

import com.mts.backend.domain.order.OrderDiscountEntity;
import com.mts.backend.domain.order.identifier.OrderDiscountId;
import com.mts.backend.domain.order.identifier.OrderId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface JpaOrderDiscountRepository extends JpaRepository<OrderDiscountEntity, Long> {

  @Query("select (count(o) > 0) from OrderDiscountEntity o where o.orderEntity.id = :id")
  boolean existsByOrder_Id(@Param("id") Long id);
  

  @Query("select o from OrderDiscountEntity o where o.orderEntity.id = :id and o.discount.id = :id1")
  Optional<OrderDiscountEntity> findByOrder_IdAndDiscount_Id(@Param("id") Long id, @Param("id1") Long id1);

  @Query("select (count(o) > 0) from OrderDiscountEntity o where o.discount.id = :id")
  boolean existsByDiscount_Id(@Param("id") @NonNull Long id);


}