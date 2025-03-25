package com.mts.backend.domain.payment.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;

public class PaymentMethodId implements Identifiable {

    private final int value;

    private PaymentMethodId(int value) {

        if (value <= 0) {
            throw new IllegalArgumentException("Payment method id must be greater than 0");
        }

        if (value > IdentifiableProvider.TINYINT_UNSIGNED_MAX) {
            throw new IllegalArgumentException("Payment method id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        }
        this.value = value;
    }
    
    public static PaymentMethodId of(int value) {
        return new PaymentMethodId(value);
    }
    
    public static PaymentMethodId of(String value) {
        return new PaymentMethodId(Integer.parseInt(value));
    }
    
    public static PaymentMethodId create(){
        return new PaymentMethodId(IdentifiableProvider.generateTimeBasedUnsignedTinyInt());
    }
    
    public int getValue() {
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
        
        PaymentMethodId paymentMethodId = (PaymentMethodId) o;
        
        return value == paymentMethodId.value;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
    
}
    
    
