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
    private final LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    public ProductPrice(@NonNull ProductPriceId id, @NonNull ProductId productId, @NonNull ProductSizeId productSizeId, @NonNull Money price,  @Nullable LocalDateTime updatedAt ) {
        super(id);
        
        Objects.requireNonNull(productId, "productId không được null");
        Objects.requireNonNull(productSizeId, "sizeId không được null");
        Objects.requireNonNull(price, "price không được null");

        this.productId = productId;
        this.productSizeId = productSizeId;
        this.price = price;
        this.updatedAt = updatedAt == null ? LocalDateTime.now() : updatedAt;
    }

    /**
     * Cập nhật giá
     */
    public void changePrice(Money newPrice) {

        Money validPrice = (Money) checkAndAssign(Money.create(newPrice.getValue()));

        if (!businessErrors.isEmpty()) {
            throw new DomainBusinessLogicException(businessErrors);
        }

        this.price = validPrice;
        this.updatedAt = LocalDateTime.now();

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

