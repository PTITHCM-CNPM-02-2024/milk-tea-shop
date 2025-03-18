package com.mts.backend.domain.product;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.product.entity.ProductPrice;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.identifier.ProductPriceId;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.value_object.ProductName;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import com.mts.backend.shared.exception.DomainBusinessLogicException;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.*;

public class Product extends AbstractAggregateRoot<ProductId> {

    private ProductName name;
    private String description;
    private CategoryId categoryId;
    private boolean available;
    private boolean signature;
    private String imagePath;
    private final LocalDateTime createdAt;
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
        
        if (prices != null && !prices.isEmpty()) {
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
            throw new DuplicateException("Giá sản phẩm đã tồn tại");
        }
        
        ProductPrice newPrice = new ProductPrice(ProductPriceId.create(), getId(), productPrice.getSizeId(), productPrice.getPrice(), LocalDateTime.now(), LocalDateTime.now());
        
        prices.add(newPrice);
        
        this.updatedAt = LocalDateTime.now();

    }

    public void changePrice(ProductPriceId productPriceId, Money newPrice) {
        ProductPrice productPrice = prices.stream()
                .filter(p -> p.getId().equals(productPriceId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Không tìm thấy giá sản phẩm"));
        
        productPrice.changePrice(newPrice);
        
        this.updatedAt = LocalDateTime.now();
    }
    
    public void changePrice(ProductSizeId productSizeId, Money newPrice) {
        ProductPrice productPrice = prices.stream()
                .filter(p -> p.getSizeId().equals(productSizeId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Không tìm thấy giá sản phẩm"));
        
        productPrice.changePrice(newPrice);
        
        this.updatedAt = LocalDateTime.now();
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

    public boolean changeName(String name) {
        ProductName newName = (ProductName) checkAndAssign(ProductName.create(name));
        if (!businessErrors.isEmpty()){
            throw new DomainBusinessLogicException(businessErrors);
        }
        
        if (this.name.equals(newName)) {
            return false;
        }
        
        this.name = newName;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeName(ProductName name) {
        Objects.requireNonNull(name, "Product name is required");
        if (this.name.equals(name)) {
            return false;
        }
        
        this.name = name;
        this.updatedAt = LocalDateTime.now();
        return true;
    }


    public Set<ProductPrice> getPrices() {
        return Collections.unmodifiableSet(prices);
    }
    
    public void changeDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void changeImagePath(String imagePath) {
        this.imagePath = imagePath;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void changeSignature(boolean signature) {
        this.signature = signature;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean changeCategory(CategoryId categoryId) {
        Objects.requireNonNull(categoryId, "Category id is required");
        
        if (this.categoryId != null && this.categoryId.equals(categoryId)) {
            return false;
        }
        
        this.categoryId = categoryId;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public void delete(ProductSizeId productSizeId){
        ProductPrice productPrice = prices.stream()
                .filter(p -> p.getSizeId().equals(productSizeId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Không tìm thấy giá sản phẩm"));
        
        prices.remove(productPrice);
        
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isOrdered() {
        return available && !prices.isEmpty();
    }
}
    
    

    