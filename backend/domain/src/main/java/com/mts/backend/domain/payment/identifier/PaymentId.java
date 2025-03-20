package com.mts.backend.domain.payment.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;

public class PaymentId implements Identifiable {
    
    private final long id;
    
    private PaymentId(long id) {
        
        if (id <= 0) {
            throw new IllegalArgumentException("Payment id must be greater than 0");
        }
        
        if (id > IdentifiableProvider.INT_UNSIGNED_MAX)
            throw new IllegalArgumentException("Payment id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        this.id = id;
    }
    
    public static PaymentId of(long id) {
        return new PaymentId(id);
    }
    
    public static PaymentId of(String id) {
        return new PaymentId(Long.parseLong(id));
    }
    
    public static PaymentId create(){
        return new PaymentId(IdentifiableProvider.generateTimeBasedUnsignedInt());
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
        if (!(o instanceof PaymentId paymentId)) return false;
        return id == paymentId.id;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
    
    
    
}
