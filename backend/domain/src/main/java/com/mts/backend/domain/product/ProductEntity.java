package com.mts.backend.domain.product;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.identifier.ProductPriceId;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.value_object.ProductName;
import com.mts.backend.shared.exception.DuplicateException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.Nullable;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
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
@Builder
public class ProductEntity extends BaseEntity<Integer> {
    @Id
    @Comment("Mã sản phẩm")
    @Column(name = "product_id", nullable = false)
    @NotNull
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã danh mục")
    @JoinColumn(name = "category_id")
    @Nullable
    private CategoryEntity categoryEntity;
    
    public Optional<CategoryEntity> getCategoryEntity() {
        return Optional.ofNullable(categoryEntity);
    }

    @Comment("Tên sản phẩm")
    @Column(name = "name", nullable = false, length = 100)
    @Convert(converter = ProductName.ProductNameConverter.class)
    @NotNull
    private ProductName name;

    @Comment("Mô tả sản phẩm")
    @Column(name = "description", length = 1000)
    @Nullable
    private String description;

    @Comment("Sản phẩm có sẵn (1: Có, 0: Không)")
    @ColumnDefault("1")
    @Column(name = "is_available")
    private Boolean available;

    @Comment("Sản phẩm đặc trưng (1: Có, 0: Không)")
    @ColumnDefault("0")
    @Column(name = "is_signature")
    private Boolean signature;

    @Comment("Đường dẫn mô tả hình ảnh")
    @Column(name = "image_path", length = 1000)
    private String imagePath;

    @OneToMany(mappedBy = "productEntity",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @Builder.Default
    private Set<ProductPriceEntity> productPrices = new LinkedHashSet<>();
    
    
    public boolean changeName(ProductName name) {
        if (this.name.equals(name)) {
            return false;
        }
        this.name = name;
        return true;
    }
    
    public boolean changeDescription(String description) {
        if (Objects.equals(this.description, description)) {
            return false;
        }
        this.description = description;
        return true;
    }
    
    public boolean changeImagePath(String imagePath) {
        if (Objects.equals(this.imagePath, imagePath)) {
            return false;
        }
        this.imagePath = imagePath;
        return true;
    }
    
    public Set<ProductPriceEntity> getProductPriceEntities() {
        return Set.copyOf(productPrices);
    }
    
    
    public ProductPriceEntity addProductPriceEntity(ProductPriceEntity price){
        Objects.requireNonNull(price, "Product price is required");
        
        if (productPrices.contains(price)) {
            throw new DuplicateException("Giá sản phẩm đã tồn tại");
        }
        
        var productPrice = findBySizeId(price.getSize().getId());

        productPrice.ifPresent(productPriceEntity -> productPrices.remove(productPriceEntity));
        
        ProductPriceEntity productPriceEntity = ProductPriceEntity.builder()
                .productEntity(this)
                .size(price.getSize())
                .price(price.getPrice())
                .id(ProductPriceId.create().getValue())
                .build();
        
        productPrices.add(productPriceEntity);
        
        return productPriceEntity;
    }
    
    public boolean removeProductPriceEntity(ProductPriceEntity price) {
        Objects.requireNonNull(price, "Product price is required");
        
        if (!productPrices.contains(price)) {
            return false;
        }
        
        productPrices.remove(price);
        return true;
    }
    
    public boolean removeProductPriceEntity(ProductSizeId sizeId) {
        Objects.requireNonNull(sizeId, "Product size id is required");
        
        var productPrice = findBySizeId(sizeId.getValue());
        
        if (productPrice.isEmpty()) {
            return false;
        }
        
        productPrices.remove(productPrice.get());
        return true;
    }
    
    public boolean changeProductPrice(ProductSizeId sizeId, Money price) {
        Objects.requireNonNull(sizeId, "Product size id is required");
        Objects.requireNonNull(price, "Product price is required");
        
        var productPrice = findBySizeId(sizeId.getValue());
        
        if (productPrice.isEmpty()) {
            return false;
        }
        
        productPrice.get().setPrice(price);
        return true;
    }
    
    
    
    private Optional<ProductPriceEntity> findBySizeId(Integer sizeId) {
        Objects.requireNonNull(sizeId, "Product size id is required");
        return productPrices.stream()
                .filter(productPrice -> productPrice.getSize().getId().equals(sizeId))
                .findFirst();
    }
    
    private Optional<ProductPriceEntity> getProductPriceEntity(Long productPriceId) {
        Objects.requireNonNull(productPriceId, "Product price id is required");
        return productPrices.stream()
                .filter(productPrice -> productPrice.getId().equals(productPriceId))
                .findFirst();
    }
    
    public Optional<ProductPriceEntity> getProductPriceEntity(ProductPriceId productPriceId) {
        Objects.requireNonNull(productPriceId, "Product price id is required");
        return getProductPriceEntity(productPriceId.getValue());
    }
    
    public Optional<ProductPriceEntity> getProductPriceEntity(ProductSizeId productSizeId) {
        Objects.requireNonNull(productSizeId, "Product size id is required");
        return getProductPriceEntity(productSizeId.getValue());
    }
    
    public Optional<ProductPriceEntity> getProductPriceEntity(Integer productSizeId) {
        Objects.requireNonNull(productSizeId, "Product size id is required");
        return productPrices.stream()
                .filter(productPrice -> productPrice.getSize().getId().equals(productSizeId))
                .findFirst();
    }
    
    public boolean isOrdered() {
        return available.booleanValue() && !productPrices.isEmpty();
    }
    
    public Money getMinPrice(){
        return productPrices.stream()
                .map(ProductPriceEntity::getPrice)
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
        ProductEntity that = (ProductEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}