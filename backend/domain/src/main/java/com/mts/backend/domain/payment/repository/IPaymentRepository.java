package com.mts.backend.domain.payment.repository;

import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.order.value_object.PaymentStatus;
import com.mts.backend.domain.payment.Payment;
import com.mts.backend.domain.payment.identifier.PaymentId;

import java.util.List;
import java.util.Optional;

public interface IPaymentRepository {
    
    Optional<Payment> findById(PaymentId id);
    
    Payment save(Payment payment);
    
    List<Payment> findAll();
    
    void delete(Payment payment);
    
    List<Payment> findByOrderId(OrderId orderId, OrderStatus status, PaymentStatus paymentStatus);

    List<Payment> findByOrderId(OrderId orderId);
}
