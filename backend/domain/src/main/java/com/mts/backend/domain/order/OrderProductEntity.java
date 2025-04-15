package com.mts.backend.domain.order;

import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.product.ProductPriceEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Getter
@Setter
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductEntity extends BaseEntity<Long> {
    @Id
    @Comment("Mã chi tiết đơn hàng")
    @Column(name = "order_product_id", columnDefinition = "int UNSIGNED")
    @NotNull
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Mã đơn hàng")
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull
    private OrderEntity orderEntity;

    @Comment("Số lượng")
    @Column(name = "quantity", columnDefinition = "smallint UNSIGNED", nullable = false)
    @NotNull
    @Min(value = 1, message = "Số lượng sản phẩm phải lớn hơn 0")
    @ColumnDefault("1")
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Mã giá sản phẩm")
    @JoinColumn(name = "product_price_id", nullable = false)
    @NotNull
    private ProductPriceEntity productPriceEntity;

    @Comment("Tùy chọn cho việc lựa chọn lượng đá, đường")
    @Column(name = "`option`", length = 500)
    private String option;


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        OrderProductEntity that = (OrderProductEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}