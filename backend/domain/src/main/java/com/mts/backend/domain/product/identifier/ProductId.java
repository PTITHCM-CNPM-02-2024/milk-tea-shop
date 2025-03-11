package com.mts.backend.domain.product.identifier;

import com.mts.backend.shared.domain.Identifiable;

import java.util.Objects;
import java.util.UUID;

public class ProductId implements Identifiable {
    private final int value;
    
    private ProductId(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Product id phải lớn hơn 0");
        }
        this.value = value;
    }
    
    public static ProductId of (int value) {
        return new ProductId(value);
    }
    
    public static ProductId of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Product id cannot be null");
        }
        return new ProductId(Integer.parseInt(value));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ProductId productId = (ProductId) o;
        
        return Objects.equals(value, productId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public int getValue() {
        return value;
    }

    /**
     * TODO: Đây là phương thức cần kiểm ra lại
     */
    public static ProductId create(){
        int random = Math.abs(UUID.randomUUID().hashCode());
        return new ProductId(random);
    }
}
