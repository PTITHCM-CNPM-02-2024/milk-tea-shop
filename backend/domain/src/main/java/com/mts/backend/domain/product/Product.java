package com.mts.backend.domain.product;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.identifier.ProductPriceId;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.value_object.ProductName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.Nullable;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;



@Entity
@Table(name = "product", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "category_id", columnList = "category_id")
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity<Integer> {
    @Id
    @Comment("Mã sản phẩm")
    @Column(name = "product_id", nullable = false)
    @NotNull
    @Getter
    private Integer id;

    private static Set<ProductPrice> $default$productPrices() {
        return new LinkedHashSet<>();
    }

    public static ProductBuilder builder() {
        return new ProductBuilder();
    }

    public boolean setId(@NotNull ProductId id) {
        if (ProductId.of(this.id).equals(id)) {
            return false;
        }
        this.id = id.getValue();
        return true;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã danh mục")
    @JoinColumn(name = "category_id")
    @Nullable
    private Category category;

    public boolean setCategory(@Nullable Category category) {
        if (Objects.equals(this.category, category)) {
            return false;
        }
        this.category = category;
        return true;
    }

    public Optional<Category> getCategory() {
        return Optional.ofNullable(category);
    }

    @Comment("Tên sản phẩm")
    @Column(name = "name", nullable = false, length = 100)
    @NotNull
    @Size(max = 100, message = "Tên sản phẩm không được vượt quá 100 ký tự")
    @jakarta.validation.constraints.NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    public ProductName getName() {
        return ProductName.of(name);
    }

    @Comment("Mô tả sản phẩm")
    @Column(name = "description", length = 1000)
    @Nullable
    @Getter
    @Setter
    private String description;

    @Comment("Sản phẩm có sẵn (1: Có, 0: Không)")
    @ColumnDefault("1")
    @Column(name = "is_available")
    @Getter
    @Setter
    private Boolean available;

    @Comment("Sản phẩm đặc trưng (1: Có, 0: Không)")
    @ColumnDefault("0")
    @Column(name = "is_signature")
    @Getter
    @Setter
    private Boolean signature;

    @Comment("Đường dẫn mô tả hình ảnh")
    @Column(name = "image_path", length = 1000)
    @Getter
    private String imagePath;

    @OneToMany(mappedBy = "product",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    private Set<ProductPrice> productPrices = new LinkedHashSet<>();


    public boolean setName(@NotNull ProductName name) {
        if (ProductName.of(name.getValue()).equals(this.getName())) {
            return false;
        }
        this.name = name.getValue();
        return true;
    }

    public boolean setImagePath(@Nullable String imagePath) {
        if (Objects.equals(this.imagePath, imagePath)) {
            return false;
        }
        this.imagePath = imagePath;
        return true;
    }

    public Set<ProductPrice> getProductPriceEntities() {
        return Set.copyOf(productPrices);
    }


    public ProductPrice addPrice(ProductPrice price) {
        Objects.requireNonNull(price, "Product price is required");

        if (productPrices.contains(price)) {
            return price;
        }

        var productPrice = findBySizeId(price.getSize().getId());

        productPrice.ifPresent(productPriceEntity -> productPrices.remove(productPriceEntity));

        ProductPrice newPrice = ProductPrice.builder()
                .product(this)
                .size(price.getSize())
                .price(price.getPrice())
                .id(ProductPriceId.create().getValue())
                .build();

        productPrices.add(newPrice);

        return newPrice;
    }

    public boolean removePrice(ProductPrice price) {
        Objects.requireNonNull(price, "Product price is required");

        if (!productPrices.contains(price)) {
            return false;
        }

        productPrices.remove(price);
        return true;
    }

    public boolean removePrice(ProductSizeId sizeId) {
        Objects.requireNonNull(sizeId, "Product size id is required");

        var price = findBySizeId(sizeId.getValue());

        if (price.isEmpty()) {
            return false;
        }

        productPrices.remove(price.get());
        return true;
    }

    public boolean setPrice(@NotNull ProductSizeId sizeId, @NotNull Money price) {

        var oldPrice = findBySizeId(sizeId.getValue());

        if (oldPrice.isEmpty()) {
            return false;
        }

        oldPrice.get().setPrice(price);
        return true;
    }


    private Optional<ProductPrice> findBySizeId(Integer sizeId) {
        Objects.requireNonNull(sizeId, "Product size id is required");
        return productPrices.stream()
                .filter(productPrice -> productPrice.getSize().getId().equals(sizeId))
                .findFirst();
    }

    private Optional<ProductPrice> getProductPriceEntity(Long productPriceId) {
        Objects.requireNonNull(productPriceId, "Product price id is required");
        return productPrices.stream()
                .filter(productPrice -> productPrice.getId().equals(productPriceId))
                .findFirst();
    }

    public Optional<ProductPrice> getProductPriceEntity(ProductPriceId productPriceId) {
        Objects.requireNonNull(productPriceId, "Product price id is required");
        return getProductPriceEntity(productPriceId.getValue());
    }

    public Optional<ProductPrice> getProductPriceEntity(ProductSizeId productSizeId) {
        Objects.requireNonNull(productSizeId, "Product size id is required");
        return getProductPriceEntity(productSizeId.getValue());
    }

    public Optional<ProductPrice> getProductPriceEntity(Integer productSizeId) {
        Objects.requireNonNull(productSizeId, "Product size id is required");
        return productPrices.stream()
                .filter(productPrice -> productPrice.getSize().getId().equals(productSizeId))
                .findFirst();
    }

    public boolean isOrdered() {
        return available.booleanValue() && !productPrices.isEmpty();
    }

    public Money getMinPrice() {
        return productPrices.stream()
                .map(ProductPrice::getPrice)
                .min(Money::compareTo)
                .orElse(Money.ZERO);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Product that = (Product) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public static class ProductBuilder {
        private @NotNull Integer id;
        private Category category;
        private @NotNull
        @Size(max = 100, message = "Tên sản phẩm không được vượt quá 100 ký tự")
        @NotBlank(message = "Tên sản phẩm không được để trống") String name;
        private String description;
        private Boolean available;
        private Boolean signature;
        private String imagePath;
        private Set<ProductPrice> productPrices$value;
        private boolean productPrices$set;

        ProductBuilder() {
        }

        public ProductBuilder id(@NotNull Integer id) {
            this.id = id;
            return this;
        }

        public ProductBuilder category(Category category) {
            this.category = category;
            return this;
        }

        public ProductBuilder name(@NotNull ProductName name) {
            this.name = name.getValue();
            return this;
        }

        public ProductBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ProductBuilder available(Boolean available) {
            this.available = available;
            return this;
        }

        public ProductBuilder signature(Boolean signature) {
            this.signature = signature;
            return this;
        }

        public ProductBuilder imagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public ProductBuilder productPrices(Set<ProductPrice> productPrices) {
            this.productPrices$value = productPrices;
            this.productPrices$set = true;
            return this;
        }

        public Product build() {
            Set<ProductPrice> productPrices$value = this.productPrices$value;
            if (!this.productPrices$set) {
                productPrices$value = Product.$default$productPrices();
            }
            return new Product(this.id, this.category, this.name, this.description, this.available, this.signature, this.imagePath, productPrices$value);
        }

        public String toString() {
            return "Product.ProductBuilder(id=" + this.id + ", category=" + this.category + ", name=" + this.name + ", description=" + this.description + ", available=" + this.available + ", signature=" + this.signature + ", imagePath=" + this.imagePath + ", productPrices$value=" + this.productPrices$value + ")";
        }
    }
}