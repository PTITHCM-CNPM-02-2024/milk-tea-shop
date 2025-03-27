package com.mts.backend.domain.store.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;

import java.io.Serializable;

@Value
public class AreaId implements Serializable {
    int value;
    
    private AreaId(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Invalid areaId");
        }
        if (value > IdentifiableProvider.SMALLINT_UNSIGNED_MAX) {
            throw new IllegalArgumentException("Invalid areaId");
        }
        this.value = value;
    }
    
    public static AreaId of (int areaId) {
        return new AreaId(areaId);
    }
    
    public static AreaId of (String areaId) {
        return new AreaId(Integer.parseInt(areaId));
    }
    
    public static AreaId create(){
        return new AreaId(IdentifiableProvider.generateTimeBasedUnsignedSmallInt());
    }
    
    public static final class AreaIdConverter implements AttributeConverter<AreaId, Integer> {
        @Override
        public Integer convertToDatabaseColumn(AreaId attribute) {
            return attribute.getValue();
        }
        
        @Override
        public AreaId convertToEntityAttribute(Integer dbData) {
            return AreaId.of(dbData);
        }
    }
    
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
