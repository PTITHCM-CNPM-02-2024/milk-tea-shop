package com.mts.backend.domain.order.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;

import java.io.Serializable;

@Value
public class OrderTableId implements Serializable {
    Long value;
    
    private OrderTableId(long value) {
        
        if (value <= 0) {
            throw new IllegalArgumentException("OrderTableId id must be greater than 0");
        }
        
        if (value > IdentifiableProvider.INT_UNSIGNED_MAX)
            throw new IllegalArgumentException("OrderTableId id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        this.value = value;
    }
    
    public static OrderTableId of(long id) {
        return new OrderTableId(id);
    }
    
    public static OrderTableId of(String id) {
        return new OrderTableId(Long.parseLong(id));
    }
    
    public static OrderTableId of(Long id) {
        return new OrderTableId(id);
    }
    
    public static OrderTableId create(){
        return new OrderTableId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
    
    public final static class OrderTableIdConverter implements AttributeConverter<OrderTableId, Long> {
        @Override
        public Long convertToDatabaseColumn(OrderTableId attribute) {
            return attribute.getValue();
        }
        
        @Override
        public OrderTableId convertToEntityAttribute(Long dbData) {
            return OrderTableId.of(dbData);
        }
    }
}
