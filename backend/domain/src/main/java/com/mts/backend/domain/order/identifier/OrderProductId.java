package com.mts.backend.domain.order.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.shared.domain.Identifiable;

public class OrderProductId implements Identifiable {
    
    private final CouponId id;
    
    private OrderProductId(CouponId id) {
        
        if (id <= 0) {
            throw new IllegalArgumentException("OrderProductId id must be greater than 0");
        }
        
        if (id > IdentifiableProvider.INT_UNSIGNED_MAX)
            throw new IllegalArgumentException("OrderProductId id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        this.id = id;
    }
    
    public static OrderProductId of(CouponId id) {
        return new OrderProductId(id);
    }
    
    public static OrderProductId of(String id) {
        return new OrderProductId(Long.parseLong(id));
    }
    
    public static OrderProductId create(){
        return new OrderProductId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
    
    public CouponId getValue() {
        return id;
    }
    
    @Override
    public String toString() {
        return String.valueOf(id);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderProductId orderProductId)) return false;
        return id == orderProductId.id;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
