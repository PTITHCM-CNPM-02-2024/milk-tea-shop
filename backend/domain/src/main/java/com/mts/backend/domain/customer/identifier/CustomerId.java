package com.mts.backend.domain.customer.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.shared.domain.Identifiable;

public class CustomerId implements Identifiable {
    private final CouponId id;
    
    private CustomerId(CouponId id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        if (id > IdentifiableProvider.INT_UNSIGNED_MAX) {
            throw new IllegalArgumentException("Id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        }
        this.id = id;
    }
    
    public static CustomerId of(CouponId id) {
        return new CustomerId(id);
    }
    
    public static CustomerId of(String id) {
        return new CustomerId(Long.parseLong(id));
    }
    
    public static CustomerId create() {
        return new CustomerId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
    
    public CouponId getValue() {
        return id;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        CustomerId customerId = (CustomerId) o;
        
        return id == customerId.id;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
    
    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
