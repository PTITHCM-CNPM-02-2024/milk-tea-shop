package com.mts.backend.domain.staff.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;

import java.io.Serializable;

@Value
public class EmployeeId implements Serializable {
    long value;
    
    private EmployeeId(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        if (value > IdentifiableProvider.INT_UNSIGNED_MAX) {
            throw new IllegalArgumentException("Id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        }
        
        this.value = value;
    }
    
    public static EmployeeId of(long value) {
        return new EmployeeId(value);
    }
    
    public static EmployeeId of(String value) {
        return new EmployeeId(Long.parseLong(value));
    }
    
    public static EmployeeId create() {
        return new EmployeeId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
    
    public static final class EmployeeIdConverter implements AttributeConverter<EmployeeId, Long> {
        @Override
        public Long convertToDatabaseColumn(EmployeeId employeeId) {
            return employeeId.getValue();
        }
        
        @Override
        public EmployeeId convertToEntityAttribute(Long value) {
            return EmployeeId.of(value);
        }
    }
}
