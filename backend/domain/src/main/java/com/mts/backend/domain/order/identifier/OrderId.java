package com.mts.backend.domain.order.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;

public class OrderId implements Identifiable {
    private final long id;
    
    private OrderId(long id) {
        
        if (id <= 0) {
            throw new IllegalArgumentException("Order id must be greater than 0");
        }
        
        if (id > IdentifiableProvider.INT_UNSIGNED_MAX)
            throw new IllegalArgumentException("Order id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        this.id = id;
    }
    
    public static OrderId of(long id) {
        return new OrderId(id);
    }
    
    public static OrderId of(String id) {
        return new OrderId(Long.parseLong(id));
    }
    
    public static OrderId create(){
        return new OrderId(IdentifiableProvider.generateTimeBasedUnsignedInt());
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
        if (!(o instanceof OrderId orderId)) return false;
        return id == orderId.id;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
    
}
