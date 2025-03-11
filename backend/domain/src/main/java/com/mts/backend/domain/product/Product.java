package com.mts.backend.domain.product;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.entity.ProductPrice;
import com.mts.backend.domain.product.identifier.ProductPriceId;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.value_object.ProductName;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import com.mts.backend.shared.exception.DomainBusinessLogicException;
import com.mts.backend.shared.exception.DomainException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.*;

public class Product extends AbstractAggregateRoot<ProductId> {

    private ProductName name;
    private String description;
    private final CategoryId categoryId;
    private boolean available;
    private boolean signature;
    private String imagePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private final Set<ProductPrice> prices = new HashSet<>();

    public Product(@NonNull ProductId productId, @NonNull ProductName name,@Nullable String description, @Nullable String imagePath, boolean available, boolean signature, @Nullable CategoryId categoryId,@Nullable Set<ProductPrice> prices,@Nullable LocalDateTime createdAt,@Nullable LocalDateTime updatedAt) {
        super(productId);
        Objects.requireNonNull(name, "Tên sản phẩm không được null");
        this.name = name;
        this.description = description == null ? "" : description;
        this.imagePath = imagePath == null ? "" : imagePath;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
        this.updatedAt = updatedAt == null ? LocalDateTime.now() : updatedAt;
        this.categoryId = categoryId;
        this.available = available;
        this.signature = signature;
        
        if (prices != null) {
            this.prices.addAll(prices);
        }
    }

    public void changeAvailable(boolean available) {
        this.available = available;
        this.updatedAt = LocalDateTime.now();
    }

    private Optional<ProductPrice> findPriceBySizeId(ProductSizeId productSizeId) {
        return prices.stream()
                .filter(p -> p.getSizeId().equals(productSizeId))
                .findFirst();
    }
    
    public void addPrice(ProductPrice productPrice) {
        
        Optional<ProductPrice> existingPrice = findPriceBySizeId(productPrice.getSizeId());
        if (existingPrice.isPresent()) {
            throw new DomainException("Giá sản phẩm cho kích thước này đã tồn tại");
        }
        
        ProductPrice newPrice = new ProductPrice(ProductPriceId.create(), getId(), productPrice.getSizeId(), productPrice.getPrice(), LocalDateTime.now(), LocalDateTime.now());
        
        prices.add(newPrice);
        
        this.updatedAt = LocalDateTime.now();

    }

    public void changePrice(ProductPriceId productPriceId, Money newPrice) {
        ProductPrice productPrice = prices.stream()
                .filter(p -> p.getId().equals(productPriceId))
                .findFirst()
                .orElseThrow(() -> new DomainException("Không tìm thấy giá sản phẩm"));
        
        productPrice.changePrice(newPrice);
        
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addExistingPrice(ProductPrice productPrice) {
        prices.add(productPrice);
    }

    public String getDescription() {
        return description;
    }

    public Optional<CategoryId> getCategoryId() {
        return Optional.ofNullable(categoryId);
    }

    public ProductName getName() {
        return name;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isSignature() {
        return signature;
    }

    public String getImagePath() {
        return imagePath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void changeName(String name) {
        this.name = (ProductName) checkAndAssign(ProductName.create(name));
        if (!businessErrors.isEmpty()) {
            throw new DomainBusinessLogicException(businessErrors);
        }
        this.updatedAt = LocalDateTime.now();
    }


    public Set<ProductPrice> getPrices() {
        return Collections.unmodifiableSet(prices);
    }
    
    public boolean isOrdered() {
        return available && !prices.isEmpty();
    }
}
    
    

    