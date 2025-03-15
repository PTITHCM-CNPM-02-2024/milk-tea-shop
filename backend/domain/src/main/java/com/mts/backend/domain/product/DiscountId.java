package com.mts.backend.domain.product;

import com.mts.backend.shared.domain.Identifiable;

import java.util.Objects;

public class DiscountId implements Identifiable {
    private final long value;
    
    private DiscountId(long value) {
        this.value = value;
    }
    
    public static DiscountId of (long value) {
        return new DiscountId(value);
    }
    
    public static DiscountId of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Discount id cannot be null");
        }
        return new DiscountId(Long.parseLong(value));
    }
    
    public long getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        DiscountId discountId = (DiscountId) o;
        
        return value == discountId.value;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
    
}
