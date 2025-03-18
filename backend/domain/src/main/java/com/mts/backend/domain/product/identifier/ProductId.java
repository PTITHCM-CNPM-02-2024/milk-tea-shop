package com.mts.backend.domain.product.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;

import java.util.Objects;

public class ProductId implements Identifiable {
    private final int value;

    private ProductId(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Product id phải lớn hơn 0");
        }
        int MAX_VALUE = IdentifiableProvider.MEDIUMINT_UNSIGNED_MAX;
        if (value > MAX_VALUE) {
            throw new IllegalArgumentException("Product id phải nhỏ hơn " + MAX_VALUE);
        }
        this.value = value;
    }
    
    public static ProductId of (int value) {
        return new ProductId(value);
    }
    
    public static ProductId of(String value) {
        Objects.requireNonNull(value, "value is required");
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
     * FIX : smallint
     */
    public static ProductId create(){
        return new ProductId(IdentifiableProvider.generateTimeBasedUnsignedMediumInt());
    }
}
