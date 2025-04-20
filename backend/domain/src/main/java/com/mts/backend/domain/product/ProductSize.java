package com.mts.backend.domain.product;

import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.value_object.ProductSizeName;
import com.mts.backend.domain.product.value_object.QuantityOfProductSize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.Optional;


@Entity
@Table(name = "product_size", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "product_size_pk", columnNames = {"unit_id", "name"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
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
public class ProductSize extends BaseEntity<Integer> {
    @Id
    @Comment("Mã kích thước")
    @Column(name = "size_id", columnDefinition = "smallint UNSIGNED")
    @NotNull
    @Getter
    private Integer id;

    public static ProductSizeBuilder builder() {
        return new ProductSizeBuilder();
    }

    public boolean setId(@NotNull ProductSizeId id) {
        if (ProductSizeId.of(this.id).equals(id)) {
            return false;
        }
        this.id = id.getValue();
        return true;
    }


    @Comment("Tên kích thước (ví dụ: S, M, L)")
    @Column(name = "name", nullable = false, length = 5)
    @NotNull
    @NotBlank(message = "Tên kích thước không được để trống")
    @Size(max = 5, message = "Tên kích thước không được vượt quá 5 ký tự")
    private String name;


    public ProductSizeName getName() {
        return ProductSizeName.of(name);
    }

    @Comment("Số lượng")
    @Column(name = "quantity", columnDefinition = "smallint UNSIGNED")
    @NotNull
    @PositiveOrZero(message = "Số lượng không được âm")
    private Integer quantity;

    public QuantityOfProductSize getQuantity() {
        return QuantityOfProductSize.of(quantity);
    }

    @Comment("Mô tả")
    @Column(name = "description", length = 1000)
    @Nullable
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã đơn vị tính")
    @JoinColumn(name = "unit_id", nullable = false)
    @NotNull
    @Getter
    private UnitOfMeasure unit;
    
    public boolean setUnit(@NotNull UnitOfMeasure unit) {
        if (this.unit.equals(unit)) {
            return false;
        }
        this.unit = unit;
        return true;
    }
    
    


    public boolean setQuantity(@NotNull QuantityOfProductSize quantity) {
        if (QuantityOfProductSize.of(this.quantity).equals(quantity)) {
            return false;
        }
        this.quantity = quantity.getValue();
        return true;
    }

    public boolean setName(@NotNull ProductSizeName name) {
        if (ProductSizeName.of(this.name).equals(name)) {
            return false;
        }
        this.name = name.getValue();
        return true;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public boolean setDescription(@Nullable String description) {
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
        ProductSize that = (ProductSize) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public static class ProductSizeBuilder {
        private @NotNull Integer id;
        private @NotNull
        @NotBlank(message = "Tên kích thước không được để trống")
        @Size(max = 5, message = "Tên kích thước không được vượt quá 5 ký tự") String name;
        private @NotNull
        @PositiveOrZero(message = "Số lượng không được âm") Integer quantity;
        private String description;
        private @NotNull UnitOfMeasure unit;

        ProductSizeBuilder() {
        }

        public ProductSizeBuilder id(@NotNull Integer id) {
            this.id = id;
            return this;
        }

        public ProductSizeBuilder name(@NotNull ProductSizeName name) {
            this.name = name.getValue();
            return this;
        }

        public ProductSizeBuilder quantity(@NotNull QuantityOfProductSize quantity) {
            this.quantity = quantity.getValue();
            return this;
        }

        public ProductSizeBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ProductSizeBuilder unit(@NotNull UnitOfMeasure unit) {
            this.unit = unit;
            return this;
        }

        public ProductSize build() {
            return new ProductSize(this.id, this.name, this.quantity, this.description, this.unit);
        }

        public String toString() {
            return "ProductSize.ProductSizeBuilder(id=" + this.id + ", name=" + this.name + ", quantity=" + this.quantity + ", description=" + this.description + ", unit=" + this.unit + ")";
        }
    }
}