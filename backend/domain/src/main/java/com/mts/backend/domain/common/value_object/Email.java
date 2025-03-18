package com.mts.backend.domain.common.value_object;

import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class Email extends AbstractValueObject {
    
    private final String value;
    
    private final static Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    
    private Email(String value){
        this.value = value;
    }
    
    public static ValueObjectValidationResult create(String value){
        Objects.requireNonNull(value, "Email is required");
        List<String> errors = new ArrayList<>();
        
        if (value.isBlank()) {
            errors.add("Email không được để trống");
        }
        
        if (value.length() > 255) {
            errors.add("Email quá dài (tối đa 255 ký tự)");
        }
        
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            errors.add("Email không hợp lệ");
        }
        
        if (errors.isEmpty()) {
            return new ValueObjectValidationResult(new Email(value), errors);
        }
        
        return new ValueObjectValidationResult(null, errors);
    }
    
    public static Email of (String value){
        ValueObjectValidationResult result = create(value);
        if (result.getBusinessErrors().isEmpty()) {
            return new Email(value);
        }
        throw new IllegalArgumentException("Email không hợp lệ: " + result.getBusinessErrors());
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
    /**
     * @return 
     */
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(value);
    }
}
