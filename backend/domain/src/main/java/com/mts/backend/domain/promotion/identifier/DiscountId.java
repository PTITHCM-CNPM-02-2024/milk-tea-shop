package com.mts.backend.domain.promotion.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;

public class DiscountId implements Identifiable {
    private final long value;
    
    private  DiscountId(long value){
        this.value = value;
    }
    
    public static DiscountId of(long value){
        if (value <= 0){
            throw new IllegalArgumentException("DiscountId must be greater than 0");
        }
        
        if (value > IdentifiableProvider.INT_UNSIGNED_MAX){
            throw new IllegalArgumentException("DiscountId must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        }
        
        return new DiscountId(value);
    }
    
    public static DiscountId of(String value){
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
        
        DiscountId discountId = (DiscountId) o;
        
        return value == discountId.value;
    }
    
    
    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }
    
    public static DiscountId create(){
        return new DiscountId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
}
