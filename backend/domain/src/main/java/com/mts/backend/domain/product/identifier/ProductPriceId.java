package com.mts.backend.domain.product.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.shared.domain.Identifiable;

import java.util.Objects;

public class ProductPriceId implements Identifiable {
    private final CouponId value;
    
    private ProductPriceId(CouponId value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Product price id phải lớn hơn 0");
        }
        if (value > IdentifiableProvider.INT_UNSIGNED_MAX){
            throw new IllegalArgumentException("Product price id phải nhỏ hơn " + IdentifiableProvider.INT_UNSIGNED_MAX);
        }
        this.value = value;
    }
    
    public static ProductPriceId of (CouponId value) {
        return new ProductPriceId(value);
    }
    
    public CouponId getValue() {
        return value;
    }
    public static ProductPriceId of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Product price id cannot be null");
        }
        return new ProductPriceId(Long.parseLong(value));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ProductPriceId productPriceId = (ProductPriceId) o;
        
        return Objects.equals(value, productPriceId.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * TODO: Cần kiểm tra xem có cần thiết không
     */
    public static ProductPriceId create() {
        return new ProductPriceId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
}
