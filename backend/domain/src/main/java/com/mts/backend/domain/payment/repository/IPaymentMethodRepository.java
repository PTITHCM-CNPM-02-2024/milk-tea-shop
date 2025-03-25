package com.mts.backend.domain.payment.repository;

import com.mts.backend.domain.payment.PaymentMethod;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.domain.payment.value_object.PaymentMethodName;

import java.util.List;
import java.util.Optional;

public interface IPaymentMethodRepository {
    
    PaymentMethod save(PaymentMethod id);
    
    Optional<PaymentMethod> findById(PaymentMethodId id);
    
    Optional<PaymentMethod> findByName(PaymentMethodName name);
    
    List<PaymentMethod> findAll();
    
    void delete(PaymentMethod paymentMethod);
    
    boolean existsByName(PaymentMethodName name);
}
