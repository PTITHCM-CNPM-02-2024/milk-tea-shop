package com.mts.backend.domain.payment;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.order.OrderEntity;
import com.mts.backend.domain.order.value_object.PaymentStatus;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.shared.exception.DomainException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
@Entity
@Table(name = "Payment", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "payment_method_id", columnList = "payment_method_id")
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@Builder
public class PaymentEntity extends BaseEntity<Long> {
    @Id
    @Comment("Mã thanh toán")
    @Column(name = "payment_id", columnDefinition = "int UNSIGNED")
    @NotNull
    private Long id;

    @Comment("Số tiền đã trả")
    @Column(name = "amount_paid", nullable = false, precision = 11, scale = 3)
    @Nullable
    @Convert(converter = Money.MoneyConverter.class)
    private Money amountPaid;
    
    public Optional<Money> getAmountPaid() {
        return Optional.ofNullable(amountPaid);
    }
    
    public Optional<Money> getChangeAmount() {
        return Optional.ofNullable(changeAmount);
    }

    @Comment("Tiền thừa")
    @ColumnDefault("0.00")
    @Column(name = "change_amount", precision = 11, scale = 3)
    @Convert(converter = Money.MoneyConverter.class)
    @Nullable
    private Money changeAmount;

    @Comment("Thời gian thanh toán")
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "payment_time")
    @Nullable
    private Instant paymentTime;

    @ManyToOne
    @Comment("Mã phương thức thanh toán")
    @JoinColumn(name = "payment_method_id")
    @NotNull
    private PaymentMethodEntity paymentMethod;

    @ManyToOne
    @Comment("Mã đơn hàng")
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull
    private OrderEntity orderEntity;

    @Comment("Trạng thái thanh toán")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status;

    public boolean changeStatus(PaymentStatus status) {
        canModifyPayment();
        if (Objects.equals(this.status, status)) {
            return false;
        }
        this.status = status;
        return true;
    }

    public PaymentEntity(Long id, @Nullable Money amountPaid, @Nullable Money changeAmount,
                         @Nullable Instant paymentTime, @NotNull PaymentMethodEntity paymentMethod, @NotNull OrderEntity orderEntity, PaymentStatus status) {
        this.id = id;
        this.amountPaid = amountPaid;
        this.changeAmount = changeAmount;
        this.paymentTime = paymentTime;
        this.paymentMethod = paymentMethod;
        this.orderEntity = orderEntity;
        this.status = status;
    }

    public PaymentEntity() {
    }
    
    public boolean changeAmountPaid(Money amountPaid) {
        canModifyPayment();
        if (Objects.equals(this.amountPaid, amountPaid)) {
            return false;
        }
        this.amountPaid = amountPaid;
        return true;
    }
    
    public boolean changeChangeAmount(Money changeAmount) {
        canModifyPayment();
        if (Objects.equals(this.changeAmount, changeAmount)) {
            return false;
        }
        this.changeAmount = changeAmount;
        return true;
    }
    
    public void cancel(){
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
        PaymentEntity that = (PaymentEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
    
    
    private void canModifyPayment(){
        if (status == PaymentStatus.PAID || status == PaymentStatus.CANCELLED) {
            throw new DomainException("Thanh toán đã được thực hiện hoặc đã hủy, không thể sửa đổi" + status);
        }
    }
}