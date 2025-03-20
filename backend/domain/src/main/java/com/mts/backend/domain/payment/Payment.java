package com.mts.backend.domain.payment;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.payment.identifier.PaymentId;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

public class Payment extends AbstractAggregateRoot<PaymentId> {
    @NonNull
    private OrderId orderId;
    @NonNull
    private PaymentMethodId paymentMethodId;
    @NonNull
    private Money amountPaid;
    @NonNull
    private Money changeAmount;
    @NonNull
    private Instant paymentTime;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Payment(@NonNull PaymentId id,@NonNull OrderId orderId,@NonNull PaymentMethodId paymentMethodId,
                   @NonNull Money amountPaid,
                    @NonNull Money changeAmount, @NonNull Instant paymentTime, LocalDateTime updatedAt) {
        super(id);
        this.orderId = Objects.requireNonNull(orderId, "Order id is required");
        this.paymentMethodId = Objects.requireNonNull(paymentMethodId, "Payment method id is required");
        this.amountPaid = Objects.requireNonNull(amountPaid, "Amount paid is required");
        this.changeAmount = Objects.requireNonNull(changeAmount, "Change amount is required");
        this.paymentTime = Objects.requireNonNull(paymentTime, "Payment time is required");
        this.createdAt = LocalDateTime.now();
        this.updatedAt = updatedAt == null ? LocalDateTime.now() : updatedAt;
    }
    
    public OrderId getOrderId() {
        return orderId;
    }
    
    public PaymentMethodId getPaymentMethodId() {
        return paymentMethodId;
    }
    
    public Money getAmountPaid() {
        return amountPaid;
    }
    
    public Money getChangeAmount() {
        return changeAmount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public boolean changePaymentMethod(PaymentMethodId paymentMethodId) {
        if (this.paymentMethodId.equals(paymentMethodId)) {
            return false;
        }
        this.paymentMethodId = paymentMethodId;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeAmountPaid(Money amountPaid) {
        if (this.amountPaid.equals(amountPaid)) {
            return false;
        }
        this.amountPaid = amountPaid;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeChangeAmount(Money changeAmount) {
        if (this.changeAmount.equals(changeAmount)) {
            return false;
        }
        this.changeAmount = changeAmount;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public Instant getPaymentTime() {
        return paymentTime;
    }
    
    
}
