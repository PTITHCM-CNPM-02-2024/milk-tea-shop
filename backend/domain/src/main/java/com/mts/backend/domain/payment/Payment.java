package com.mts.backend.domain.payment;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.order.Order;
import com.mts.backend.domain.order.value_object.PaymentStatus;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.shared.exception.DomainException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "payment", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "payment_method_id", columnList = "payment_method_id")
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})

@NamedEntityGraphs({
        @NamedEntityGraph(name = "graph.payment.fetchPmt",
                attributeNodes = {
                        @NamedAttributeNode("paymentMethod"),
                })
})
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity<Long> {
    @Id
    @Comment("Mã thanh toán")
    @Column(name = "payment_id", columnDefinition = "int UNSIGNED")
    @NotNull
    @Getter
    private Long id;

    @Comment("Số tiền đã trả")
    @Column(name = "amount_paid", nullable = false, precision = 11, scale = 3)
    @Nullable
    private BigDecimal amountPaid;

    public static PaymentBuilder builder() {
        return new PaymentBuilder();
    }


    public Optional<Money> getAmountPaid() {
        return Optional.ofNullable(amountPaid).map(Money::of);
    }

    public Optional<Money> getChangeAmount() {
        return Optional.ofNullable(changeAmount).map(Money::of);
    }

    @Comment("Tiền thừa")
    @ColumnDefault("0.00")
    @Column(name = "change_amount", precision = 11, scale = 3)
    @Nullable
    private BigDecimal changeAmount;

    @Comment("Thời gian thanh toán")
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "payment_time")
    @NotNull
    @Getter
    @Setter
    private Instant paymentTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã phương thức thanh toán")
    @JoinColumn(name = "payment_method_id")
    @NotNull
    @Getter
    private PaymentMethod paymentMethod;
    
    public boolean setPaymentMethod(@NotNull PaymentMethod paymentMethod) {
        if (this.paymentMethod.equals(paymentMethod)) {
            return false;
        }
        this.paymentMethod = paymentMethod;
        return true;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã đơn hàng")
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull
    @Getter
    private Order order;

    public boolean setOrder(@NotNull Order order) {
        if (this.order.equals(order)) {
            return false;
        }
        this.order = order;
        return true;
    }

    @Comment("Trạng thái thanh toán")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status;

    public boolean setStatus(PaymentStatus status) {
        canModifyPayment();
        if (Objects.equals(this.status, status)) {
            return false;
        }
        this.status = status;
        return true;
    }

    public boolean setAmountPaid(@Nullable Money amountPaid) {
        canModifyPayment();
        if (Objects.equals(Money.of(this.amountPaid), amountPaid)) {
            return false;
        }
        this.amountPaid = amountPaid == null ? null : amountPaid.getValue();
        return true;
    }

    public boolean setChangeAmount(@Nullable Money changeAmount) {
        canModifyPayment();
        if (Objects.equals(Money.of(this.changeAmount), changeAmount)) {
            return false;
        }
        this.changeAmount = changeAmount == null ? null : changeAmount.getValue();
        return true;
    }

    public void cancel() {
        canModifyPayment();
        this.status = PaymentStatus.CANCELLED;
    }

    public Optional<PaymentStatus> getStatus() {
        return Optional.ofNullable(status);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Payment that = (Payment) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }


    private void canModifyPayment() {
        if (status == PaymentStatus.PAID || status == PaymentStatus.CANCELLED) {
            throw new DomainException("Thanh toán đã được thực hiện hoặc đã hủy, không thể sửa đổi" + status);
        }
    }

    public static class PaymentBuilder {
        private @NotNull Long id;
        private BigDecimal amountPaid;
        private BigDecimal changeAmount;
        private @NotNull Instant paymentTime;
        private @NotNull PaymentMethod paymentMethod;
        private @NotNull Order order;
        private PaymentStatus status;

        PaymentBuilder() {
        }

        public PaymentBuilder id(@NotNull Long id) {
            this.id = id;
            return this;
        }

        public PaymentBuilder amountPaid(@Nullable Money amountPaid) {
            this.amountPaid = amountPaid == null ? null : amountPaid.getValue();
            return this;
        }

        public PaymentBuilder changeAmount(@Nullable Money changeAmount) {
            this.changeAmount = changeAmount == null ? null : changeAmount.getValue();
            return this;
        }

        public PaymentBuilder paymentTime(@NotNull Instant paymentTime) {
            this.paymentTime = paymentTime;
            return this;
        }

        public PaymentBuilder paymentMethod(@NotNull PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public PaymentBuilder orderEntity(@NotNull Order order) {
            this.order = order;
            return this;
        }

        public PaymentBuilder status(PaymentStatus status) {
            this.status = status;
            return this;
        }

        public Payment build() {
            return new Payment(this.id, this.amountPaid, this.changeAmount, this.paymentTime, this.paymentMethod, this.order, this.status);
        }

        public String toString() {
            return "Payment.PaymentBuilder(id=" + this.id + ", amountPaid=" + this.amountPaid + ", changeAmount=" + this.changeAmount + ", paymentTime=" + this.paymentTime + ", paymentMethod=" + this.paymentMethod + ", order=" + this.order + ", status=" + this.status + ")";
        }
    }
}