package com.mts.backend.domain.order;

import com.mts.backend.domain.order.identifier.OrderProductId;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.product.ProductPrice;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;


@Entity
@Table(name = "order_product", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "order_id", columnList = "order_id"),
        @Index(name = "product_price_id", columnList = "product_price_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "OrderProduct_pk", columnNames = {"order_id", "product_price_id"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NoArgsConstructor
@AllArgsConstructor
public class OrderProduct extends BaseEntity<Long> {
    @Id
    @Comment("Mã chi tiết đơn hàng")
    @Column(name = "order_product_id", columnDefinition = "int UNSIGNED")
    @NotNull
    @Getter
    private Long id;

    public static OrderProductBuilder builder() {
        return new OrderProductBuilder();
    }

    public boolean setId(@NotNull OrderProductId id) {
        if (OrderProductId.of(this.id).equals(id)) {
            return false;
        }
        this.id = id.getValue();
        return true;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Mã đơn hàng")
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

    @Comment("Số lượng")
    @Column(name = "quantity", columnDefinition = "smallint UNSIGNED", nullable = false)
    @NotNull
    @Min(value = 1, message = "Số lượng sản phẩm phải lớn hơn 0")
    @ColumnDefault("1")
    @Getter
    @Setter
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Mã giá sản phẩm")
    @JoinColumn(name = "product_price_id", nullable = false)
    @NotNull
    @Getter
    private ProductPrice price;

    public boolean setPrice(@NotNull ProductPrice price) {
        if (Objects.equals(this.price, price)) {
            return false;
        }
        this.price = price;
        return true;
    }


    @Comment("Tùy chọn cho việc lựa chọn lượng đá, đường")
    @Column(name = "`option`", length = 500)
    @Nullable
    @Getter
    @Setter
    private String option;


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        OrderProduct that = (OrderProduct) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public static class OrderProductBuilder {
        private @NotNull Long id;
        private @NotNull Order order;
        private @NotNull
        @Min(value = 1, message = "Số lượng sản phẩm phải lớn hơn 0") Integer quantity;
        private @NotNull ProductPrice price;
        private String option;

        OrderProductBuilder() {
        }

        public OrderProductBuilder id(@NotNull Long id) {
            this.id = id;
            return this;
        }

        public OrderProductBuilder order(@NotNull Order order) {
            this.order = order;
            return this;
        }

        public OrderProductBuilder quantity(@NotNull @Min(value = 1, message = "Số lượng sản phẩm phải lớn hơn 0") Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public OrderProductBuilder price(@NotNull ProductPrice price) {
            this.price = price;
            return this;
        }

        public OrderProductBuilder option(String option) {
            this.option = option;
            return this;
        }

        public OrderProduct build() {
            return new OrderProduct(this.id, this.order, this.quantity, this.price, this.option);
        }

        public String toString() {
            return "OrderProduct.OrderProductBuilder(id=" + this.id + ", order=" + this.order + ", quantity=" + this.quantity + ", price=" + this.price + ", option=" + this.option + ")";
        }
    }
}