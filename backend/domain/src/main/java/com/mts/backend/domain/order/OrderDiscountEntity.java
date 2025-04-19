package com.mts.backend.domain.order;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.promotion.Discount;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "order_discount", schema = "milk_tea_shop_prod")
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDiscountEntity extends BaseEntity<Long> {
    @Id
    @Comment("Mã giảm giá đơn hàng")
    @Column(name = "order_discount_id", columnDefinition = "int UNSIGNED")
    @NotNull
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã đơn  hàng áp dụng giảm giá")
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull
    private OrderEntity orderEntity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã chương trình giảm giá được áp dụng")
    @JoinColumn(name = "discount_id", nullable = false)
    @NotNull
    private Discount discount;

    
    @Comment("Số tiền giảm giá được áp dụng")
    @Column(name = "discount_amount", nullable = false, precision = 11, scale = 3)
    @NotNull
    @Convert(converter = Money.MoneyConverter.class)
    private Money discountAmount;
    
    public boolean changeDiscountAmount(Money discountAmount) {
        if (this.discountAmount.equals(discountAmount)) {
            return false;
        }
        this.discountAmount = discountAmount;
        return true;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        OrderDiscountEntity that = (OrderDiscountEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}