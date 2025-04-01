package com.mts.backend.domain.payment.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;

import java.io.Serializable;

@Value
public class PaymentId implements Serializable {
    
    Long value;
    
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
    
    public static PaymentId of(Long value) {
        return new PaymentId(value);
    }
    
    public static PaymentId create(){
        return new PaymentId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
    
    public static final class PaymentIdConverter implements AttributeConverter<PaymentId, Long> {
        
        @Override
        public Long convertToDatabaseColumn(PaymentId paymentId) {
            return paymentId.getValue();
        }
        
        @Override
        public PaymentId convertToEntityAttribute(Long value) {
            return PaymentId.of(value);
        }
    }
    
}
