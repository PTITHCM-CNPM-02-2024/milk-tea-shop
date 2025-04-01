package com.mts.backend.domain.order.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;

import java.io.Serializable;

@Value
public class OrderId implements Serializable {
    
    Long value;
    
    private OrderId (long value) {
        
        if (value <= 0) {
            throw new IllegalArgumentException("Order id must be greater than 0");
        }
        
        if (value > IdentifiableProvider.INT_UNSIGNED_MAX){
            throw new IllegalArgumentException("Order id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        }
        this.value = value;
    }
    
    public static OrderId of(long value) {
        return new OrderId(value);
    }
    
    public static OrderId of(Long value) {
        return new OrderId(value);
    }
    
    public static OrderId of(String value) {
        return new OrderId(Long.parseLong(value));
    }
    
    public static OrderId create(){
        return new OrderId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
    
    public static final class OrderIdConverter implements AttributeConverter<OrderId, Long> {
        @Override
        public Long convertToDatabaseColumn(OrderId attribute) {
            return attribute.getValue();
        }
        
        @Override
        public OrderId convertToEntityAttribute(Long dbData) {
            return OrderId.of(dbData);
        }
    }
}
