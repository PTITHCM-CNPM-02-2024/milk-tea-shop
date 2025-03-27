package com.mts.backend.domain.order.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.shared.domain.Identifiable;

public class OrderId implements Identifiable {
    
    private final CouponId value;
    
    private OrderId (CouponId value) {
        
        if (value <= 0) {
            throw new IllegalArgumentException("Order id must be greater than 0");
        }
        
        if (value > IdentifiableProvider.INT_UNSIGNED_MAX){
            throw new IllegalArgumentException("Order id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        }
        this.value = value;
    }
    
    public static OrderId of(CouponId value) {
        return new OrderId(value);
    }
    
    public static OrderId of(String value) {
        return new OrderId(Long.parseLong(value));
    }
    
    public static OrderId create(){
        return new OrderId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
    
    public CouponId getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        OrderId orderId = (OrderId) o;
        
        return value == orderId.value;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }
}
