package com.mts.backend.domain.staff.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;

import java.io.Serializable;

@Value
public class ManagerId implements Serializable {
    long value;
    
    private ManagerId(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        if (value > IdentifiableProvider.INT_UNSIGNED_MAX) {
            throw new IllegalArgumentException("Id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        }
        
        this.value = value;
    }
    
    public static ManagerId of(long value) {
        return new ManagerId(value);
    }
    
    public static ManagerId of(String value) {
        return new ManagerId(Long.parseLong(value));
    }
    
    public static ManagerId create() {
        return new ManagerId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
    
    
    public static final class ManagerIdConverter implements AttributeConverter<ManagerId, Long> {
        @Override
        public Long convertToDatabaseColumn(ManagerId managerId) {
            return managerId.getValue();
        }
        
        @Override
        public ManagerId convertToEntityAttribute(Long value) {
            return ManagerId.of(value);
        }
    }
}
