package com.mts.backend.domain.promotion.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;

public class CouponId implements Identifiable {
    private final long value;
    
    private  CouponId(long value){
        this.value = value;
    }
    
    public static CouponId of(long value){
        if (value <= 0){
            throw new IllegalArgumentException("CouponId must be greater than 0");
        }
        
        if (value > IdentifiableProvider.INT_UNSIGNED_MAX){
            throw new IllegalArgumentException("CouponId must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        }
        
        return new CouponId(value);
    }
    
    public static CouponId of(String value){
        return of(Long.parseLong(value));
    }
    
    public long getValue() {
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
        
        CouponId couponId = (CouponId) o;
        
        return value == couponId.value;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }
    
    public static CouponId create(){
        return new CouponId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
}
