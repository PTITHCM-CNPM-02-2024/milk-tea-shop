package com.mts.backend.domain.payment.jpa;

import com.mts.backend.domain.payment.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface JpaPaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {
    @Query("select (count(p) > 0) from PaymentMethod p where p.name = :paymentName")
    boolean existsByPaymentName(@Param("paymentName") @NonNull String paymentName);

    @Query("select p from PaymentMethod p where p.name = :paymentName")
    Optional<PaymentMethod> findByPaymentName(@Param("paymentName") @NonNull String paymentName);

    @Query("select (count(p) > 0) from PaymentMethod p where p.id <> :id and p.name = :paymentName")
    boolean existsByIdNotAndPaymentName(@Param("id") @NonNull Integer id, @Param("paymentName") @NonNull String paymentName);


}