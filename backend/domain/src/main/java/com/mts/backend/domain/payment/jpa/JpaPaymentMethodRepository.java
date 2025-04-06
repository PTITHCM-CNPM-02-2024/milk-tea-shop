package com.mts.backend.domain.payment.jpa;

import com.mts.backend.domain.payment.PaymentMethodEntity;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.domain.payment.value_object.PaymentMethodName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface JpaPaymentMethodRepository extends JpaRepository<PaymentMethodEntity, Integer> {
    @Query("select (count(p) > 0) from PaymentMethodEntity p where p.paymentName = :paymentName")
    boolean existsByPaymentName(@Param("paymentName") @NonNull PaymentMethodName paymentName);

    @Query("select p from PaymentMethodEntity p where p.paymentName = :paymentName")
    Optional<PaymentMethodEntity> findByPaymentName(@Param("paymentName") @NonNull PaymentMethodName paymentName);

    @Query("select (count(p) > 0) from PaymentMethodEntity p where p.id <> :id and p.paymentName = :paymentName")
    boolean existsByIdNotAndPaymentName(@Param("id") @NonNull Integer id, @Param("paymentName") @NonNull PaymentMethodName paymentName);


}