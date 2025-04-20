package com.mts.backend.domain.product;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.product.identifier.ProductPriceId;
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
public class ProductPrice extends BaseEntity<Long> {
    @Id
    @Comment("Mã giá sản phẩm")
    @Column(name = "product_price_id", columnDefinition = "int UNSIGNED")
    @NotNull
    @Getter
    private Long id;

    public static ProductPriceBuilder builder() {
        return new ProductPriceBuilder();
    }

    public boolean setId(@NotNull ProductPriceId id) {
        if (ProductPriceId.of(this.id).equals(id)) {
            return false;
        }
        this.id = id.getValue();
        return true;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Mã sản phẩm")
    @JoinColumn(name = "product_id")
    @NotNull
    @Getter
    private Product product;

    public boolean setProduct(@NotNull Product product) {
        if (Objects.equals(this.product, product)) {
            return false;
        }
        this.product = product;
        return true;
    }


    @Comment("Giá")
    @Column(name = "price", nullable = false, precision = 11, scale = 3)
    @NotNull
    private BigDecimal price;

    public Money getPrice() {
        return Money.of(price);
    }

    public boolean setPrice(@NotNull Money price) {
        if (this.price.equals(price.getValue())) {
            return false;
        }
        this.price = price.getValue();
        return true;
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ProductPrice that = (ProductPrice) o;
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
    @Getter
    private ProductSize size;

    public boolean setSize(@NotNull ProductSize size) {
        if (Objects.equals(this.size, size)) {
            return false;
        }
        this.size = size;
        return true;
    }

    public static class ProductPriceBuilder {
        private @NotNull Long id;
        private @NotNull Product product;
        private @NotNull BigDecimal price;
        private @NotNull ProductSize size;

        ProductPriceBuilder() {
        }

        public ProductPriceBuilder id(@NotNull Long id) {
            this.id = id;
            return this;
        }

        public ProductPriceBuilder product(@NotNull Product product) {
            this.product = product;
            return this;
        }

        public ProductPriceBuilder price(@NotNull Money price) {
            this.price = price.getValue();
            return this;
        }

        public ProductPriceBuilder size(@NotNull ProductSize size) {
            this.size = size;
            return this;
        }

        public ProductPrice build() {
            return new ProductPrice(this.id, this.product, this.price, this.size);
        }

        public String toString() {
            return "ProductPrice.ProductPriceBuilder(id=" + this.id + ", product=" + this.product + ", price=" + this.price + ", size=" + this.size + ")";
        }
    }
}