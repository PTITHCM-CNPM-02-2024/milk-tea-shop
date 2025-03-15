package com.mts.backend.domain.product.identifier;

import com.mts.backend.shared.domain.Identifiable;

public final class ProductDiscountId implements Identifiable {
    private final long value;
    
    private ProductDiscountId(long value) {
        this.value = value;
    }
    
    public static ProductDiscountId of (long value) {
        return new ProductDiscountId(value);
    }
    
    public static ProductDiscountId of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Product discount id cannot be null");
        }
        return new ProductDiscountId(Long.parseLong(value));
    }
    
    public long getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ProductDiscountId productDiscountId = (ProductDiscountId) o;
        
        return value == productDiscountId.value;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * TODO: Cần kiểm tra xem có cần thiết không
     */
    public ProductDiscountId create() {
        long random = Math.abs(System.currentTimeMillis());
        return new ProductDiscountId(random);
    }
}