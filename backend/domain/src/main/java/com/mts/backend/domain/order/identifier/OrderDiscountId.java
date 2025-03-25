package com.mts.backend.domain.order.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;

public class OrderDiscountId implements Identifiable {
    private final long id;
    
    private OrderDiscountId(long id) {
        
        if (id <= 0) {
            throw new IllegalArgumentException("OrderDiscountId id must be greater than 0");
        }
        
        if (id > IdentifiableProvider.INT_UNSIGNED_MAX)
            throw new IllegalArgumentException("OrderDiscountId id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        this.id = id;
    }
    
    public static OrderDiscountId of(long id) {
        return new OrderDiscountId(id);
    }
    
    public static OrderDiscountId of(String id) {
        return new OrderDiscountId(Long.parseLong(id));
    }
    
    public static OrderDiscountId create(){
        return new OrderDiscountId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
    
    public long getValue() {
        return id;
    }
    
    @Override
    public String toString() {
        return String.valueOf(id);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderDiscountId orderDiscountId)) return false;
        return id == orderDiscountId.id;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
