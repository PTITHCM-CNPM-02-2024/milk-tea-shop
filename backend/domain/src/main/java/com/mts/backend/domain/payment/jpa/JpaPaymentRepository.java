package com.mts.backend.domain.payment.jpa;

import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.order.value_object.PaymentStatus;
import com.mts.backend.domain.payment.PaymentEntity;
import com.mts.backend.domain.payment.identifier.PaymentId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
  
    @EntityGraph(attributePaths = {"paymentMethod", "orderEntity"})
    @Query("select p from PaymentEntity p")
    Page<PaymentEntity> findAllFetch(Pageable pageable);
    
    @EntityGraph(attributePaths = {"paymentMethod", "orderEntity"})
    @Query("select p from PaymentEntity p where p.id = :id")
    Optional<PaymentEntity> findByIdFetch(@Param("id") Long id);
    

  @Query("select p from PaymentEntity p where p.paymentTime between :paymentTimeStart and :paymentTimeEnd")
  Page<PaymentEntity> findByPaymentTimeBetween(@Param("paymentTimeStart") @NonNull Instant paymentTimeStart, @Param("paymentTimeEnd") @NonNull Instant paymentTimeEnd, Pageable pageable);

  @Query("select p from PaymentEntity p where p.paymentTime between :paymentTimeStart and :paymentTimeEnd")
    @EntityGraph(value = "graph.payment.fetchPmt", type = EntityGraph.EntityGraphType.FETCH)
  List<PaymentEntity> findByPaymentTimeBetween(@Param("paymentTimeStart") @NonNull Instant paymentTimeStart, @Param("paymentTimeEnd") @NonNull Instant paymentTimeEnd);

  @Query("select count(p) from PaymentEntity p where p.paymentTime between :paymentTimeStart and :paymentTimeEnd")
  long countByPaymentTimeBetween(@Param("paymentTimeStart") @NonNull Instant paymentTimeStart, @Param("paymentTimeEnd") @NonNull Instant paymentTimeEnd);
  
    @Query(value = "SELECT SUM(p.amount_paid) FROM milk_tea_shop_prod.payment p WHERE p.payment_time BETWEEN " +
                   ":paymentTimeStart AND " +
                   ":paymentTimeEnd AND p.status = :#{#status.name()}", nativeQuery = true)
    BigDecimal findTotalAmountPaidByPaymentTimeBetween(@Param("paymentTimeStart") @NonNull Instant paymentTimeStart,
                                                       @Param("paymentTimeEnd") @NonNull Instant paymentTimeEnd,
                                                       @Param("status") @NonNull PaymentStatus status);

}