package com.mts.backend.domain.common.value_object;

import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class PhoneNumber extends AbstractValueObject {
    
    private final String value;
    
    private final static Pattern PHONE_NUMBER_PATTERN = Pattern.compile("(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)");
    
    private PhoneNumber(String value) {
        Objects.requireNonNull(value, "Phone number is required");
        this.value = value;
    }
    
    public static ValueObjectValidationResult create(String value) {
        Objects.requireNonNull(value, "Phone number is required");
        
        List<String> errors = new ArrayList<>();
        
        if (value.isBlank()) {
            errors.add("Số điện thoại không được để trống");
        }
        
        var normalizedValue = normalize(value);
        
        if (!PHONE_NUMBER_PATTERN.matcher(normalizedValue).matches()) {
            errors.add("Số điện thoại không hợp lệ");
        }
        
        if (errors.isEmpty()) {
            return new ValueObjectValidationResult(new PhoneNumber(normalizedValue), errors);
        }
        
        return new ValueObjectValidationResult(null, errors);
    }
    
    private static String normalize(String value) {
        return value.replaceAll("[^0-9]", "");
    }
    
    public static PhoneNumber of(String value) {
        ValueObjectValidationResult result = create(value);
        if (result.getBusinessErrors().isEmpty()) {
            return new PhoneNumber(normalize(value));
        }
        throw new IllegalArgumentException("Số điện thoại không hợp lệ: " + result.getBusinessErrors());
    }
    
    public String getValue() {
        return value;
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
        return value;
    }
    
    
}
