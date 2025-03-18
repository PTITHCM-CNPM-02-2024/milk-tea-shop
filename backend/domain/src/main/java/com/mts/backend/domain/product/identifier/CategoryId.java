package com.mts.backend.domain.product.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;

import java.util.Objects;

public class CategoryId implements Identifiable {
    private final int value;
    
    private CategoryId(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Category id phải lớn hơn 0");
        }
        if (value > IdentifiableProvider.SMALLINT_UNSIGNED_MAX){
            throw new IllegalArgumentException("Category id phải nhỏ hơn " + IdentifiableProvider.SMALLINT_UNSIGNED_MAX);
        }
        this.value = value;
    }
    
    public static CategoryId of (int value) {
        return new CategoryId(value);
    }
    
    public static CategoryId of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Category id cannot be null");
        }
        return new CategoryId(Integer.parseInt(value));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        CategoryId categoryId = (CategoryId) o;
        
        return Objects.equals(value, categoryId.value);
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
    @Override
    public String toString () {
        return String.valueOf(value);
    }
    
    public int getValue() {
        return value;
    }

    /**
     * TODO: Đây là phương thức cần kiểm ra lại
     */
    public static CategoryId create() {
        return new CategoryId(IdentifiableProvider.generateTimeBasedUnsignedSmallInt());
    }
}
