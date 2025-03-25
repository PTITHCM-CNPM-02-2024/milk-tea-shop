package com.mts.backend.infrastructure.payment.jpa;

import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.order.value_object.PaymentStatus;
import com.mts.backend.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, Long> {
  @Query("select p from PaymentEntity p where p.orderEntity.id = :id")
  List<PaymentEntity> findByOrderEntity_Id(@Param("id") Long id);

  @Query("""
          select p from PaymentEntity p
          where p.orderEntity.id is not null and p.orderEntity.id = :id and p.orderEntity.status = :status and p.status = :status1""")
  List<PaymentEntity> findByOrderAndPaymentStatus(@Param("id") Long orderId, @Param("status") OrderStatus status, @Param("status1") PaymentStatus status1);

  // Insert payment using native query and entity parameter
  @Modifying
  @Transactional
  @Query(value = "INSERT INTO milk_tea_shop_prod.Payment (payment_id, order_id, payment_method_id, amount_paid, " +
          "change_amount, payment_time, status) " +
          "VALUES (:#{#entity.id}, :#{#entity.orderEntity.id}, :#{#entity.paymentMethod.id}, :#{#entity.amountPaid}, " +
          ":#{#entity.changeAmount}, :#{#entity.paymentTime}, :#{#entity.status?.name()})", nativeQuery = true)
  void insertPayment(@Param("entity") PaymentEntity entity);

  // Update payment using native query and entity parameter
  @Modifying
  @Transactional
  @Query(value = "UPDATE milk_tea_shop_prod.Payment SET " +
          "order_id = :#{#entity.orderEntity.id}, " +
          "payment_method_id = :#{#entity.paymentMethod.id}, " +
          "amount_paid = :#{#entity.amountPaid}, " +
          "change_amount = :#{#entity.changeAmount}, " +
          "payment_time = :#{#entity.paymentTime}, " +
          "status = :#{#entity.status.name()}, " +
          "updated_at = CURRENT_TIMESTAMP " +
          "WHERE payment_id = :#{#entity.id}", nativeQuery = true)
  void updatePayment(@Param("entity") PaymentEntity entity);

  // Delete payment using native query
  @Modifying
  @Transactional
  @Query(value = "DELETE FROM milk_tea_shop_prod.Payment WHERE payment_id = :id", nativeQuery = true)
  int deletePaymentById(@Param("id") Long id);
}