package com.mts.backend.domain.payment.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.io.Serializable;

@EqualsAndHashCode
@Getter
public class PaymentMethodId implements Serializable {

    Integer value;
    
    public PaymentMethodId() {
    }

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
    
    public static PaymentMethodId of(Integer value) {
        return new PaymentMethodId(value);
    }

    public static PaymentMethodId of(String value) {
        return new PaymentMethodId(Integer.parseInt(value));
    }

    public static PaymentMethodId create() {
        return new PaymentMethodId(IdentifiableProvider.generateTimeBasedUnsignedTinyInt());
    }
    @Converter(autoApply = true)
    public static final class PaymentMethodIdConverter implements AttributeConverter<PaymentMethodId, Integer> {

        @Override
        public Integer convertToDatabaseColumn(PaymentMethodId paymentMethodId) {
            return paymentMethodId.getValue();
        }

        @Override
        public PaymentMethodId convertToEntityAttribute(Integer value) {
            return PaymentMethodId.of(value);
        }
    }
}
    
    
