package com.mts.backend.domain.payment.jpa;

import com.mts.backend.domain.persistence.entity.PaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface JpaPaymentMethodRepository extends JpaRepository<PaymentMethodEntity, Integer> {
    @Query("select (count(p) > 0) from PaymentMethodEntity p where p.paymentName = :paymentName")
    boolean existsByPaymentName(@Param("paymentName") @NonNull String paymentName);

    // Insert payment method using native query
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO milk_tea_shop_prod.PaymentMethod (payment_method_id, payment_name, payment_description) " +
            "VALUES (:#{#entity.id}, :#{#entity.paymentName}, :#{#entity.paymentDescription})", nativeQuery = true)
    void insertPaymentMethod(@Param("entity") PaymentMethodEntity entity);

    // Update payment method using native query
    @Modifying
    @Transactional
    @Query(value = "UPDATE milk_tea_shop_prod.PaymentMethod SET " +
            "payment_name = :#{#entity.paymentName}, " +
            "payment_description = :#{#entity.paymentDescription} "+
            "WHERE payment_method_id = :#{#entity.id}", nativeQuery = true)
    int updatePaymentMethod(@Param("entity") PaymentMethodEntity entity);

    @Query("select p from PaymentMethodEntity p where p.paymentName = :paymentName")
    Optional<PaymentMethodEntity> findByPaymentName(@Param("paymentName") @NonNull String paymentName);

    // Delete payment method using native query
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.PaymentMethod WHERE payment_method_id = :id", nativeQuery = true)
    void deletePaymentMethodById(@Param("id") Short id);

}