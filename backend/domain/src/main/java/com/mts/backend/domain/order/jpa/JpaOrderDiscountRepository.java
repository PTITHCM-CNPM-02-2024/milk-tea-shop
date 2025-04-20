package com.mts.backend.domain.order.jpa;

import com.mts.backend.domain.order.OrderDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface JpaOrderDiscountRepository extends JpaRepository<OrderDiscount, Long> {

  @Query("select (count(o) > 0) from OrderDiscount o where o.order.id = :id")
  boolean existsByOrder_Id(@Param("id") Long id);
  

  @Query("select o from OrderDiscount o where o.order.id = :id and o.discount.id = :id1")
  Optional<OrderDiscount> findByOrder_IdAndDiscount_Id(@Param("id") Long id, @Param("id1") Long id1);

  @Query("select (count(o) > 0) from OrderDiscount o where o.discount.id = :id")
  boolean existsByDiscount_Id(@Param("id") @NonNull Long id);


}