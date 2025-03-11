package com.mts.backend.domain.product.identifier;

import com.mts.backend.shared.domain.Identifiable;

import java.util.Objects;

public class UnitOfMeasureId implements Identifiable {
    private final int value;
    
    private UnitOfMeasureId(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Unit of measure id phải lớn hơn 0");
        }
        this.value = value;
    }
    
    public static UnitOfMeasureId of (int value) {
        return new UnitOfMeasureId(value);
    }
    
    public static UnitOfMeasureId of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Unit of measure id cannot be null");
        }
        return new UnitOfMeasureId(Integer.parseInt(value));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        UnitOfMeasureId unitOfMeasureId = (UnitOfMeasureId) o;
        
        return Objects.equals(value, unitOfMeasureId.value);
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
    @Override
    public String toString () {
        return String.valueOf(value);
    }
}
