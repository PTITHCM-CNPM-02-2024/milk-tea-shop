package com.mts.backend.domain.account.value_object;

import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class Username extends AbstractValueObject {
    
    private final String value;
    
    private final static Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,20}$");
    
    private Username(String value) {
        Objects.requireNonNull(value, "Username is required");
        this.value = value;
    }
    
    public static ValueObjectValidationResult create(String value) {
        Objects.requireNonNull(value, "Username is required");
        List<String> errors = new ArrayList<>();
        if (value.isBlank()) {
            errors.add("tên đăng nhập không được để trống");
        }
        if (value.length() < 3) {
            errors.add("tên đăng nhập quá ngắn");
        }
        if (value.length() > 20) {
            errors.add("tên đăng nhập quá dài");
        }
        if (!USERNAME_PATTERN.matcher(value).matches()) {
            errors.add("tên đăng nhập không hợp lệ");
        }
        if (errors.isEmpty()) {
            return new ValueObjectValidationResult(new Username(value), errors);
        }
        
        return new ValueObjectValidationResult(null, errors);
    }
    /**
     * @return 
     */
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(value);
    }
    
    public static Username of(String value) {
        ValueObjectValidationResult result = create(value);
        if (result.getBusinessErrors().isEmpty()) {
            return new Username(value);
        }
        throw new IllegalArgumentException("tên đăng nhập không hợp lệ: " + result.getBusinessErrors());
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
}
