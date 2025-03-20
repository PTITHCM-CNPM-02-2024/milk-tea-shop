package com.mts.backend.domain.product.entity;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.identifier.ProductPriceId;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.shared.domain.AbstractEntity;
import com.mts.backend.shared.exception.DomainBusinessLogicException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;

public class ProductPrice extends AbstractEntity<ProductPriceId> {
    private final ProductId productId;
    private final ProductSizeId productSizeId;
    private Money price;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductPrice(@NonNull ProductPriceId id, @NonNull ProductId productId, @NonNull ProductSizeId productSizeId, @NonNull Money price, @Nullable LocalDateTime updatedAt ) {
        super(id);
        
        this.productId = Objects.requireNonNull(productId, "Product id is required");
        this.productSizeId = Objects.requireNonNull(productSizeId, "Product size id is required");
        this.price = price;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = updatedAt == null ? LocalDateTime.now() : updatedAt;
    }

    /**
     * Cập nhật giá
     */
    public boolean changePrice(@NonNull Money price) {
        if (this.price.equals(price)) {
            return false;
        }
        
        this.price = price;
        this.updatedAt = LocalDateTime.now();
        return true;
    }

    public ProductId getProductId() {
        return productId;
    }

    public ProductSizeId getSizeId() {
        return productSizeId;
    }
    
    public Money getPrice() {
        return price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ProductPrice that = (ProductPrice) o;
        return productId.equals(that.productId) && productSizeId.equals(that.productSizeId);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + productId.hashCode();
        result = 31 * result + productSizeId.hashCode();
        return result;
    }
}

