package com.mts.backend.domain.payment.repository;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.order.value_object.PaymentStatus;
import com.mts.backend.domain.payment.Payment;
import com.mts.backend.domain.payment.identifier.PaymentId;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.domain.payment.jpa.JpaPaymentRepository;
import com.mts.backend.domain.persistence.entity.OrderEntity;
import com.mts.backend.domain.persistence.entity.PaymentEntity;
import com.mts.backend.domain.persistence.entity.PaymentMethodEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PaymentRepository implements IPaymentRepository {
    
    private final JpaPaymentRepository paymentRepository;
    private final JpaPaymentRepository jpaPaymentRepository;

    public PaymentRepository(JpaPaymentRepository paymentRepository, JpaPaymentRepository jpaPaymentRepository) {
        this.paymentRepository = paymentRepository;
        this.jpaPaymentRepository = jpaPaymentRepository;
    }
    /**
     * @param id 
     * @return
     */
    @Override
    public Optional<Payment> findById(PaymentId id) {
        Objects.requireNonNull(id, "Payment id is required");
        
        return paymentRepository.findById(id.getValue())
                .map(en -> new Payment(
                        PaymentId.of(en.getId()),
                        OrderId.of(en.getOrderEntity().getId()),
                        PaymentMethodId.of(en.getPaymentMethod().getId()),
                        en.getAmountPaid() == null ? null : Money.of(en.getAmountPaid()),
                        en.getChangeAmount() == null ? null : Money.of(en.getChangeAmount()),
                        en.getStatus() == null ? null : en.getStatus(),
                        en.getPaymentTime(),
                        en.getUpdatedAt().orElse(LocalDateTime.now())
                ));
    }

    /**
     * @param payment 
     * @return
     */
    @Override
    @Transactional
    public Payment save(Payment payment) {
        Objects.requireNonNull(payment, "Payment is required");
        
        try {
            if (paymentRepository.existsById(payment.getId().getValue())) {
                return update(payment);
            } else {
                return create(payment);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Không thể lưu thanh toán", e);
        }
    }
    
    @Transactional
    protected Payment create(Payment payment) {
        Objects.requireNonNull(payment, "Payment is required");

        PaymentEntity en = PaymentEntity.builder()
                .id(payment.getId().getValue())
                .amountPaid(payment.getAmountPaid().map(Money::getValue).orElse(null))
                .changeAmount(payment.getChangeAmount().map(Money::getValue).orElse(null))
                .paymentTime(payment.getPaymentTime())
                .status(payment.getStatus().orElse(null))
                .build();

        OrderEntity orderEntity = OrderEntity.builder()
                .id(payment.getOrderId().getValue())
                .build();

        PaymentMethodEntity paymentMethodEntity = PaymentMethodEntity.builder()
                .id(payment.getPaymentMethodId().getValue())
                .build();
        
        en.setOrderEntity(orderEntity);
        en.setPaymentMethod(paymentMethodEntity);
        
        paymentRepository.insertPayment(en);
        
        return payment;
    }
    
    
    @Transactional
    protected Payment update(Payment payment) {
        Objects.requireNonNull(payment, "Payment is required");

        PaymentEntity en = PaymentEntity.builder()
                .id(payment.getId().getValue())
                .amountPaid(payment.getAmountPaid().map(Money::getValue).orElse(null))
                .changeAmount(payment.getChangeAmount().map(Money::getValue).orElse(null))
                .paymentTime(payment.getPaymentTime())
                .status(payment.getStatus().orElse(null))
                .build();

        OrderEntity orderEntity = OrderEntity.builder()
                .id(payment.getOrderId().getValue())
                .build();
        
        PaymentMethodEntity paymentMethodEntity = PaymentMethodEntity.builder()
                .id(payment.getPaymentMethodId().getValue())
                .build();
        
        en.setPaymentMethod(paymentMethodEntity);
        
        en.setOrderEntity(orderEntity);
        
        paymentRepository.updatePayment(en);
        
        return payment;
    }
    

    /**
     * @return 
     */
    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll().stream()
                .map(en -> new Payment(
                        PaymentId.of(en.getId()),
                        OrderId.of(en.getOrderEntity().getId()),
                        PaymentMethodId.of(en.getPaymentMethod().getId()),
                        en.getAmountPaid() == null ? null : Money.of(en.getAmountPaid()),
                        en.getChangeAmount() == null ? null : Money.of(en.getChangeAmount()),
                        en.getStatus() == null ? null : en.getStatus(),
                        en.getPaymentTime(),
                        en.getUpdatedAt().orElse(LocalDateTime.now())
                ))
                .toList();
    }

    /**
     * @param payment 
     */
    @Override
    public void delete(Payment payment) {

    }

    /**
     * @param orderId 
     * @return
     */
    @Override
    public List<Payment> findByOrderId(OrderId orderId, OrderStatus orderStatus, PaymentStatus status) {
        Objects.requireNonNull(orderId, "Order id is required");
        
        return paymentRepository.findByOrderAndPaymentStatus(orderId.getValue(), orderStatus, status).stream()
                .map(en -> new Payment(
                        PaymentId.of(en.getId()),
                        OrderId.of(en.getOrderEntity().getId()),
                        PaymentMethodId.of(en.getPaymentMethod().getId()),
                        en.getAmountPaid() == null ? null : Money.of(en.getAmountPaid()),
                        en.getChangeAmount() == null ? null : Money.of(en.getChangeAmount()),
                        en.getStatus() == null ? null : en.getStatus(),
                        en.getPaymentTime(),
                        en.getUpdatedAt().orElse(LocalDateTime.now())
                ))
                .toList();
    }
    
    @Override
    public List<Payment> findByOrderId(OrderId orderId) {
        Objects.requireNonNull(orderId, "Order id is required");
        
        return paymentRepository.findByOrderEntity_Id(orderId.getValue()).stream()
                .map(en -> new Payment(
                        PaymentId.of(en.getId()),
                        OrderId.of(en.getOrderEntity().getId()),
                        PaymentMethodId.of(en.getPaymentMethod().getId()),
                        en.getAmountPaid() == null ? null : Money.of(en.getAmountPaid()),
                        en.getChangeAmount() == null ? null : Money.of(en.getChangeAmount()),
                        en.getStatus() == null ? null : en.getStatus(),
                        en.getPaymentTime(),
                        en.getUpdatedAt().orElse(LocalDateTime.now())
                ))
                .toList();
    }
}
