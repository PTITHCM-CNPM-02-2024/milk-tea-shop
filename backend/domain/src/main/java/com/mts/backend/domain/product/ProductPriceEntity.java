package com.mts.backend.domain.product;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.product.identifier.ProductPriceId;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "product_price", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "product_id", columnList = "product_id"),
        @Index(name = "size_id", columnList = "size_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "ProductPrice_pk", columnNames = {"product_id", "size_id"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPriceEntity extends BaseEntity<Long> {
    @Id
    @Comment("Mã giá sản phẩm")
    @Column(name = "product_price_id", columnDefinition = "int UNSIGNED")
    @NotNull
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Mã sản phẩm")
    @JoinColumn(name = "product_id")
    @NotNull
    private ProductEntity productEntity;

    @Comment("Giá")
    @Column(name = "price", nullable = false, precision = 11, scale = 3)
    @Convert(converter = Money.MoneyConverter.class)
    @NotNull
    private Money price;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ProductPriceEntity that = (ProductPriceEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Mã kích thước")
    @JoinColumn(name = "size_id")
    @NotNull
    private ProductSizeEntity size;

}