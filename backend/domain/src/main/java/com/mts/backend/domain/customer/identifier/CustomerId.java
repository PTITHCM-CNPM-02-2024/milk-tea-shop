package com.mts.backend.domain.customer.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;

import java.io.Serializable;
@Value
public class CustomerId implements Serializable {
   Long value;
    
    private CustomerId(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        if (value > IdentifiableProvider.INT_UNSIGNED_MAX) {
            throw new IllegalArgumentException("Id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        }
        this.value = value;
    }
    
    public static CustomerId of(long id) {
        return new CustomerId(id);
    }
    
    public static CustomerId of(Long id) {
        return new CustomerId(id);
    }
    
    public static CustomerId of(String id) {
        return new CustomerId(Long.parseLong(id));
    }
    
    public static CustomerId create() {
        return new CustomerId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
    
    public static final class CustomerIdConverter implements AttributeConverter<CustomerId, Long> {
        @Override
        public Long convertToDatabaseColumn(CustomerId attribute) {
            return attribute.getValue();
        }
        
        @Override
        public CustomerId convertToEntityAttribute(Long dbData) {
            return CustomerId.of(dbData);
        }
    }
}
