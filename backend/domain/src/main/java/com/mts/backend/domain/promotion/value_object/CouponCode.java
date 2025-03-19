package com.mts.backend.domain.promotion.value_object;


import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class CouponCode extends AbstractValueObject {
    private final String value;
    private final static Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{3,15}$");
    private CouponCode(String value){
        this.value = normalize(value);
    }
    
    public static ValueObjectValidationResult create(String value){
        Objects.requireNonNull(value, "Coupon code must not be null");
        
        List<String> errors = new ArrayList<>();
        
        if (!pattern.matcher(value).matches()){
            errors.add("Mã coupon phải chứa ít nhất 3 ký tự và không quá 15 ký tự");
        }
        
        if( errors.isEmpty()){
            return new ValueObjectValidationResult(new CouponCode(value), errors);
        }
        
        return new ValueObjectValidationResult(null, errors);
    }
    
    public static CouponCode of(String value){
        ValueObjectValidationResult result = create(value);
        
        if (result.getBusinessErrors().isEmpty()){
            return new CouponCode(value);
        }
        
        throw new IllegalArgumentException("Mã coupon không hợp lệ: " + result.getBusinessErrors());
    }
    
    private static String normalize(String value){
        return value.trim().toUpperCase();
    }
    /**
     * @return 
     */
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(value);
    }
    
    @Override
    public String toString() {
        return normalize(value);
    }
    
    public String getValue() {
        return normalize(value);
    }
    
    
    
    
}
