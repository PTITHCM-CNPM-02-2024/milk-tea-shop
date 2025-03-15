package com.mts.backend.domain.product.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;

import java.util.Objects;

public class UnitOfMeasureId implements Identifiable {
    private final int value;
    
    private UnitOfMeasureId(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Unit of measure id phải lớn hơn 0");
        }
        if (value > IdentifiableProvider.SMALLINT_UNSIGNED_MAX){
            throw new IllegalArgumentException("Unit of measure id phải nhỏ hơn " + IdentifiableProvider.SMALLINT_UNSIGNED_MAX);
        }
        this.value = value;
    }
    
    public static UnitOfMeasureId of (int value) {
        return new UnitOfMeasureId(value);
    }
    
    public static UnitOfMeasureId of(String value) {
        Objects.requireNonNull(value, "value is required");
        return new UnitOfMeasureId(Integer.parseInt(value));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        UnitOfMeasureId unitOfMeasureId = (UnitOfMeasureId) o;
        
        return Objects.equals(value, unitOfMeasureId.value);
    }
    
    public int getValue() {
        return value;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
    @Override
    public String toString () {
        return String.valueOf(value);
    }
    
    // TODO: cần kiểm tra lại phương thức này
    public static UnitOfMeasureId create(){
        return new UnitOfMeasureId(IdentifiableProvider.generateTimeBasedUnsignedSmallInt());
    }
}
