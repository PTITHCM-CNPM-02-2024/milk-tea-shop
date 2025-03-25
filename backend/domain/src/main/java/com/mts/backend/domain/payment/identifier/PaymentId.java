package com.mts.backend.domain.payment.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;

public class PaymentId implements Identifiable {
    
    private final long value;
    
    private PaymentId (long value) {
        
        if (value <= 0) {
            throw new IllegalArgumentException("Payment id must be greater than 0");
        }
        
        if (value > IdentifiableProvider.INT_UNSIGNED_MAX){
            throw new IllegalArgumentException("Payment id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        }
        this.value = value;
    }
    
    public static PaymentId of(long value) {
        return new PaymentId(value);
    }
    
    public static PaymentId of(String value) {
        return new PaymentId(Long.parseLong(value));
    }
    
    public static PaymentId create(){
        return new PaymentId(IdentifiableProvider.generateTimeBasedUnsignedInt());
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
        
        PaymentId paymentId = (PaymentId) o;
        
        return value == paymentId.value;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }
}
