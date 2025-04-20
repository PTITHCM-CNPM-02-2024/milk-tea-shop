package com.mts.backend.domain.order;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.order.identifier.OrderDiscountId;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.promotion.Discount;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "order_discount", schema = "milk_tea_shop_prod")
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NoArgsConstructor
@AllArgsConstructor
public class OrderDiscount extends BaseEntity<Long> {
    @Id
    @Comment("Mã giảm giá đơn hàng")
    @Column(name = "order_discount_id", columnDefinition = "int UNSIGNED")
    @NotNull
    @Getter
    private Long id;

    public static OrderDiscountBuilder builder() {
        return new OrderDiscountBuilder();
    }

    private boolean setId(@NotNull OrderDiscountId id) {
        if (OrderDiscountId.of(this.id).equals(id)) {
            return false;
        }
        this.id = id.getValue();
        return true;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã đơn  hàng áp dụng giảm giá")
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull
    @Getter
    private Order order;

    public boolean setOrder(@NotNull Order order) {
        if (Objects.equals(this.order, order)) {
            return false;
        }
        this.order = order;
        return true;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã chương trình giảm giá được áp dụng")
    @JoinColumn(name = "discount_id", nullable = false)
    @Getter
    private Discount discount;

    public boolean setDiscount(@NotNull Discount discount) {
        if (Objects.equals(this.discount, discount)) {
            return false;
        }
        this.discount = discount;
        return true;
    }


    @Comment("Số tiền giảm giá được áp dụng")
    @Column(name = "discount_amount", nullable = false, precision = 11, scale = 3)
    @NotNull
    private BigDecimal discountAmount;


    public Money getDiscountAmount() {
        return Money.of(this.discountAmount);
    }

    public boolean setDiscountAmount(@NotNull Money discountAmount) {
        if (Money.of(this.discountAmount).equals(discountAmount)) {
            return false;
        }
        this.discountAmount = discountAmount.getValue();
        return true;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        OrderDiscount that = (OrderDiscount) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public static class OrderDiscountBuilder {
        private @NotNull Long id;
        private @NotNull Order order;
        private Discount discount;
        private @NotNull BigDecimal discountAmount;

        OrderDiscountBuilder() {
        }

        public OrderDiscountBuilder id(@NotNull Long id) {
            this.id = id;
            return this;
        }

        public OrderDiscountBuilder order(@NotNull Order order) {
            this.order = order;
            return this;
        }

        public OrderDiscountBuilder discount(Discount discount) {
            this.discount = discount;
            return this;
        }

        public OrderDiscountBuilder discountAmount(@NotNull Money discountAmount) {
            this.discountAmount = discountAmount.getValue();
            return this;
        }

        public OrderDiscount build() {
            return new OrderDiscount(this.id, this.order, this.discount, this.discountAmount);
        }

        public String toString() {
            return "OrderDiscount.OrderDiscountBuilder(id=" + this.id + ", order=" + this.order + ", discount=" + this.discount + ", discountAmount=" + this.discountAmount + ")";
        }
    }
}