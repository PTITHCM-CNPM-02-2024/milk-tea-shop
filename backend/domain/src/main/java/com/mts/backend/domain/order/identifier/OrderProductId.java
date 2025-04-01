package com.mts.backend.domain.order.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;

import java.io.Serializable;

@Value
public class OrderProductId implements Serializable {
    
   Long value;
    
    private OrderProductId(long value) {
        
        if (value <= 0) {
            throw new IllegalArgumentException("OrderProductId id must be greater than 0");
        }
        
        if (value > IdentifiableProvider.INT_UNSIGNED_MAX)
            throw new IllegalArgumentException("OrderProductId id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        this.value = value;
    }
    
    public static OrderProductId of(long id) {
        return new OrderProductId(id);
    }
    
    public static OrderProductId of(String id) {
        return new OrderProductId(Long.parseLong(id));
    }
    
    public static OrderProductId of(Long id) {
        return new OrderProductId(id);
    }
    
    public static OrderProductId create(){
        return new OrderProductId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
    
    
    public static final class OrderProductIdConverter implements AttributeConverter<OrderProductId, Long> {
        @Override
        public Long convertToDatabaseColumn(OrderProductId attribute) {
            return attribute.getValue();
        }
        
        @Override
        public OrderProductId convertToEntityAttribute(Long dbData) {
            return OrderProductId.of(dbData);
        }
    }
}
