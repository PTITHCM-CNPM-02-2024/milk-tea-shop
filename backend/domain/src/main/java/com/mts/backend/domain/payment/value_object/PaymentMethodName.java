package com.mts.backend.domain.payment.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Value
@Builder
public class PaymentMethodName  {
    
    String value;
    
    private static final int MAX_LENGTH = 50;
    
    private PaymentMethodName (String value){
        Objects.requireNonNull(value, "Payment method name is required");

        List<String> businessErrors = new ArrayList<>(List.of());

        if (value.isBlank()) {
            businessErrors.add("Tên phương thức thanh toán không được để trống");
        }

        if (value.length() > MAX_LENGTH) {
            businessErrors.add("Tên phương thức thanh toán không được vượt quá " + MAX_LENGTH + " ký tự");
        }
        
        if (!businessErrors.isEmpty()){
            throw new DomainBusinessLogicException(businessErrors);
        }
        
        this.value = normalize(value);    
    }
    
    
    private static String normalize(String value){
        return value.trim().toUpperCase();
    }
    
    public static final class PaymentMethodNameConverter implements jakarta.persistence.AttributeConverter<PaymentMethodName, String> {
        
        @Override
        public String convertToDatabaseColumn(PaymentMethodName paymentMethodName) {
            return Objects.isNull(paymentMethodName) ? null : paymentMethodName.getValue();
        }
        
        @Override
        public PaymentMethodName convertToEntityAttribute(String value) {
            return Objects.isNull(value) ? null : new PaymentMethodName(value);
        }
    }
}
