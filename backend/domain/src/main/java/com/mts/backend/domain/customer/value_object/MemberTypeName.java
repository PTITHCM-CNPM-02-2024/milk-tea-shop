package com.mts.backend.domain.customer.value_object;

import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MemberTypeName extends AbstractValueObject {
    private final String value;
    private static final int MAX_LENGTH = 50;
    
    private MemberTypeName(String value) {
        this.value = normalizer(value);
    }
    
    public static ValueObjectValidationResult create(String value) {
        Objects.requireNonNull(value, "Member type name is required");
        List<String> errors = new ArrayList<>();
        
        if (value.length() > MAX_LENGTH) {
            errors.add("Tên loại thành viên không được vượt quá 50 ký tự");
        }
        
        if (value.isBlank()) {
            errors.add("Tên loại thành viên không được để trống");
        }
        
        if (errors.isEmpty()) {
            return new ValueObjectValidationResult(new MemberTypeName(value), errors);
        }
        
        return new ValueObjectValidationResult(null, errors);
    }
    
    public static MemberTypeName of(String value) {
        ValueObjectValidationResult result = create(value);
        
        if (result.getBusinessErrors().isEmpty()) {
            return new MemberTypeName(value);
        }
        
        throw new IllegalArgumentException("Tên loại thành viên không hợp lệ: " + result.getBusinessErrors());
    }
    
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(value);
    }
    
    private static String normalizer(String value) {
        return value.trim().toUpperCase();
    }
    
    public String getValue() {
        return normalizer(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    
}
