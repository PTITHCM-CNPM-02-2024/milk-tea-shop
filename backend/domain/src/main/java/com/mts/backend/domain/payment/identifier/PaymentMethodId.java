package com.mts.backend.domain.payment.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;

public class PaymentMethodId implements Identifiable {
    
    private final int id;
    
    private PaymentMethodId(int id) {
        
        if (id <= 0) {
            throw new IllegalArgumentException("PaymentMethod id must be greater than 0");
        }
        
        if (id > IdentifiableProvider.TINYINT_UNSIGNED_MAX)
            throw new IllegalArgumentException("PaymentMethod id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        this.id = id;
    }
    
    public static PaymentMethodId of(int id) {
        return new PaymentMethodId(id);
    }
    
    public static PaymentMethodId of(String id) {
        return new PaymentMethodId(Integer.parseInt(id));
    }
    
    public static PaymentMethodId create(){
        return new PaymentMethodId(IdentifiableProvider.generateTimeBasedUnsignedTinyInt());
    }
    
    public int getValue() {
        return id;
    }
    
    @Override
    public String toString() {
        return String.valueOf(id);
    }
    
    @Override
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentMethodId paymentMethodId)) return false;
        return id == paymentMethodId.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
}
