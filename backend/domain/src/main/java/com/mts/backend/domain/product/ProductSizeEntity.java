package com.mts.backend.domain.product;

import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.product.value_object.ProductSizeName;
import com.mts.backend.domain.product.value_object.QuantityOfProductSize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
@Entity
@Table(name = "ProductSize", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "ProductSize_pk", columnNames = {"unit_id", "name"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraphs(
        {
                @NamedEntityGraph(
                        name = "ProductSizeEntity.withUnit",
                        attributeNodes = {
                                @NamedAttributeNode("unit")
                        }
                )
        }
)
public class ProductSizeEntity extends BaseEntity<Integer> {
    @Id
    @Comment("Mã kích thước")
    @Column(name = "size_id", columnDefinition = "smallint UNSIGNED")
    @NotNull
    private Integer id;

    @Comment("Tên kích thước (ví dụ: S, M, L)")
    @Column(name = "name", nullable = false, length = 5)
    @NotNull
    @Convert(converter = ProductSizeName.ProductSizeNameConverter.class)
    private ProductSizeName name;

    @Comment("Số lượng")
    @Column(name = "quantity", columnDefinition = "smallint UNSIGNED")
    @NotNull
    @Convert(converter = QuantityOfProductSize.QuantityOfProductSizeConverter.class)
    private QuantityOfProductSize quantity;

    @Comment("Mô tả")
    @Column(name = "description", length = 1000)
    @Nullable
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã đơn vị tính")
    @JoinColumn(name = "unit_id", nullable = false)
    @NotNull
    private UnitOfMeasureEntity unit;


    public boolean changeQuantity(QuantityOfProductSize quantity) {
        if (this.quantity.equals(quantity)) {
            return false;
        }
        this.quantity = quantity;
        return true;
    }

    public boolean changeName(ProductSizeName name) {
        if (this.name.equals(name)) {
            return false;
        }
        this.name = name;
        return true;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public boolean changeDescription(String description) {
        if (Objects.equals(this.description, description)) {
            return false;
        }
        this.description = description;
        return true;
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ProductSizeEntity that = (ProductSizeEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}