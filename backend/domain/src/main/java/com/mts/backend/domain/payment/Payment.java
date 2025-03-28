package com.mts.backend.domain.payment;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.value_object.PaymentStatus;
import com.mts.backend.domain.payment.identifier.PaymentId;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import com.mts.backend.shared.exception.DomainException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Payment extends AbstractAggregateRoot<PaymentId> {
    @NonNull
    private final OrderId orderId;
    @NonNull
    private PaymentMethodId paymentMethodId;
    @Nullable
    private Money amountPaid;
    @Nullable
    private Money changeAmount;
    @NonNull
    private final Instant paymentTime;
    @Nullable
    private PaymentStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Payment(@NonNull PaymentId id,@NonNull OrderId orderId,@NonNull PaymentMethodId paymentMethodId,
                   @Nullable Money amountPaid,
                    @Nullable Money changeAmount,@Nullable PaymentStatus status, @NonNull Instant paymentTime,
                   LocalDateTime updatedAt) {
        super(id);
        this.orderId = Objects.requireNonNull(orderId, "Order id is required");
        this.paymentMethodId = Objects.requireNonNull(paymentMethodId, "Payment method id is required");
        this.amountPaid = amountPaid;
        this.changeAmount = changeAmount;
        this.paymentTime = Objects.requireNonNull(paymentTime, "Payment time is required");
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = updatedAt == null ? LocalDateTime.now() : updatedAt;
    }
    
    public OrderId getOrderId() {
        return orderId;
    }
    
    public PaymentMethodId getPaymentMethodId() {
        return paymentMethodId;
    }
    
    public Optional<Money> getAmountPaid() {
        return Optional.ofNullable(amountPaid);
    }
    
    public Optional<Money> getChangeAmount() {
        return Optional.ofNullable(changeAmount);
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public boolean changePaymentMethod(PaymentMethodId paymentMethodId) {
        canModifyPayment();
        if (this.paymentMethodId.equals(paymentMethodId)) {
            return false;
        }
        this.paymentMethodId = paymentMethodId;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeAmountPaid(Money amountPaid) {
        canModifyPayment();
        if (Objects.equals(this.amountPaid, amountPaid)) {
            return false;
        }
        this.amountPaid = amountPaid;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeChangeAmount(Money changeAmount) {
        canModifyPayment();
        if (Objects.equals(this.changeAmount, changeAmount)) {
            return false;
        }
        this.changeAmount = changeAmount;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public Instant getPaymentTime() {
        return paymentTime;
    }
    
    private void canModifyPayment() {
        if (status == PaymentStatus.PAID || status == PaymentStatus.CANCELLED) {
            throw new DomainException("Thanh toán đã được thực hiện hoặc đã hủy, không thể sửa đổi" + status);
        }
    }
    
    
    public boolean changeStatus(PaymentStatus status) {
        canModifyPayment();
        if (Objects.equals(this.status, status)) {
            return false;
        }
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public Optional<PaymentStatus> getStatus() {
        return Optional.ofNullable(status);
    }
    
}
