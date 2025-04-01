package com.mts.backend.domain.order.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;

import java.io.Serializable;
@Value
public class OrderDiscountId implements Serializable {
   Long value;
    
    private OrderDiscountId(long value) {
        
        if (value <= 0) {
            throw new IllegalArgumentException("OrderDiscountId id must be greater than 0");
        }
        
        if (value > IdentifiableProvider.INT_UNSIGNED_MAX)
            throw new IllegalArgumentException("OrderDiscountId id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        this.value = value;
    }
    
    public static OrderDiscountId of(long id) {
        return new OrderDiscountId(id);
    }
    
    public static OrderDiscountId of(String id) {
        return new OrderDiscountId(Long.parseLong(id));
    }
    
    public static OrderDiscountId of(Long id) {
        return new OrderDiscountId(id);
    }
    
    public static OrderDiscountId create(){
        return new OrderDiscountId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
    
    public final static class OrderDiscountIdConverter implements AttributeConverter<OrderDiscountId, Long> {
        @Override
        public Long convertToDatabaseColumn(OrderDiscountId attribute) {
            return attribute.getValue();
        }
        
        @Override
        public OrderDiscountId convertToEntityAttribute(Long dbData) {
            return OrderDiscountId.of(dbData);
        }
    }
}
