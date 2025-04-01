package com.mts.backend.domain.payment.jpa;

import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.order.value_object.PaymentStatus;
import com.mts.backend.domain.payment.PaymentEntity;
import com.mts.backend.domain.payment.identifier.PaymentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, Long> {
  @Query("select p from PaymentEntity p where p.orderEntity.id = :id")
  List<PaymentEntity> findByOrderEntity_Id(@Param("id") Long id);

  @Query("""
          select p from PaymentEntity p
          where p.orderEntity.id is not null and p.orderEntity.id = :id and p.orderEntity.status = :status and p.status = :status1""")
  List<PaymentEntity> findByOrderAndPaymentStatus(@Param("id") OrderId orderId, @Param("status") OrderStatus status,
                                                  @Param("status1") PaymentStatus status1);

  @Query("select p from PaymentEntity p where p.orderEntity.id = :id and p.id <> :id1")
  List<PaymentEntity> findByOrderEntity_IdAndIdNot(@Param("id") @NonNull Long id, @Param("id1") @NonNull Long id1);

  // Delete payment using native query
  @Modifying
  @Transactional
  @Query(value = "DELETE FROM milk_tea_shop_prod.Payment WHERE payment_id = :id", nativeQuery = true)
  int deletePaymentById(@Param("id") Long id);
}