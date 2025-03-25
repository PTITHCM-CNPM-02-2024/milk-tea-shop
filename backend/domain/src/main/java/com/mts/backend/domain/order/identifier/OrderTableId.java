package com.mts.backend.domain.order.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;

public class OrderTableId implements Identifiable {
    
    private final long id;
    
    private OrderTableId(long id) {
        
        if (id <= 0) {
            throw new IllegalArgumentException("OrderTableId id must be greater than 0");
        }
        
        if (id > IdentifiableProvider.INT_UNSIGNED_MAX)
            throw new IllegalArgumentException("OrderTableId id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        this.id = id;
    }
    
    public static OrderTableId of(long id) {
        return new OrderTableId(id);
    }
    
    public static OrderTableId of(String id) {
        return new OrderTableId(Long.parseLong(id));
    }
    
    public static OrderTableId create(){
        return new OrderTableId(IdentifiableProvider.generateTimeBasedUnsignedInt());
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
        if (!(o instanceof OrderTableId orderTableId)) return false;
        return id == orderTableId.id;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
