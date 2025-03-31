package com.mts.backend.domain.product.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import lombok.Value;

import java.io.Serializable;
import java.util.Objects;

@Value
public class UnitOfMeasureId implements Serializable {
    int value;
    
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
    
    // TODO: cần kiểm tra lại phương thức này
    public static UnitOfMeasureId create(){
        return new UnitOfMeasureId(IdentifiableProvider.generateTimeBasedUnsignedSmallInt());
    }
    
    public static final class UnitOfMeasureIdConverter implements jakarta.persistence.AttributeConverter<UnitOfMeasureId, Integer> {
        @Override
        public Integer convertToDatabaseColumn(UnitOfMeasureId attribute) {
            return attribute.getValue();
        }
        
        @Override
        public UnitOfMeasureId convertToEntityAttribute(Integer dbData) {
            return new UnitOfMeasureId(dbData);
        }
    }
}
