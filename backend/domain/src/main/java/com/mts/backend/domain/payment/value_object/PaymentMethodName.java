package com.mts.backend.domain.payment.value_object;

import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PaymentMethodName extends AbstractValueObject {
    
    private final String value;
    
    private static final int MAX_LENGTH = 50;
    
    private PaymentMethodName (String value){
        this.value = normalize(value);    
    }
    
    public static ValueObjectValidationResult create(String value){
        Objects.requireNonNull(value, "Payment method name is required");
        
        List<String> businessErrors = new ArrayList<>(List.of());
        
        if (value.isBlank()) {
            businessErrors.add("Tên phương thức thanh toán không được để trống");
        }
        
        if (value.length() > MAX_LENGTH) {
            businessErrors.add("Tên phương thức thanh toán không được vượt quá " + MAX_LENGTH + " ký tự");
        }
        
        if (businessErrors.isEmpty()){
            return new ValueObjectValidationResult(new PaymentMethodName(value), businessErrors);
        }
        
        return new ValueObjectValidationResult(null, businessErrors);
        
        
    }
    
    public static PaymentMethodName of(String value){
        ValueObjectValidationResult result = create(value);
        
        if (result.getBusinessErrors().isEmpty()){
            return new PaymentMethodName(value);
        }
        
        throw new RuntimeException(String.join(", ", result.getBusinessErrors()));
    }
    
    private static String normalize(String value){
        return value.trim().toUpperCase();
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return normalize(value);
    }


    /**
     * @return 
     */
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(value);
    }
}
