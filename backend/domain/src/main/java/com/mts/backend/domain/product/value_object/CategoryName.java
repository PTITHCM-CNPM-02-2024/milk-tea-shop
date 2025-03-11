package com.mts.backend.domain.product.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;

public class CategoryName extends AbstractValueObject {
    
    private static final int MAX_LENGTH = 100;
    
    private final String value;

    private CategoryName(String value) {
        this.value = value;
    }
    
    public static ValueObjectValidationResult create(String value) {
        List<String> businessErrors = new ArrayList<>();
        
        if (value == null || value.trim().isEmpty()) {
            businessErrors.add("Tên không được để trống");
        }
        
        if (value != null && value.length() > MAX_LENGTH) {
            businessErrors.add("Tên không được quá " + MAX_LENGTH + " ký tự");
        }
        
        if (businessErrors.isEmpty()) {
            return new ValueObjectValidationResult(new CategoryName(value), businessErrors);
        }
        
        return new ValueObjectValidationResult(null, businessErrors);
        
    }
    
    public static CategoryName of(String value) {
        ValueObjectValidationResult result = create(value);
        if (result.getBusinessErrors().isEmpty()) {
            String normalizedValue = normalize(value);
            return new CategoryName(normalizedValue);
        }
        throw new DomainBusinessLogicException(result.getBusinessErrors());
    }
    
    private static String normalize(String value) {
        return value.toUpperCase().trim();
    }
    
    public String getValue() {
        return normalize(value);
    }
    
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(value);
    }
    
    @Override
    public String toString() {
        return getValue();
    }
    
}
