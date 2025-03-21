package com.mts.backend.domain.product.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductName extends AbstractValueObject {
    
    private final String value;
    
    private static final int MAX_LENGTH = 100;
    
    private ProductName(String value) {
        this.value = normalize(value);
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
            return new ValueObjectValidationResult(new ProductName(value), businessErrors);
        }
        
        return new ValueObjectValidationResult(null, businessErrors);
    }
    
    public static ProductName of(String value) {
        ValueObjectValidationResult result = create(value);
        if (result.getBusinessErrors().isEmpty()) {
            String normalizedValue = normalize(value);
            return new ProductName(normalizedValue);
        }
        throw new DomainBusinessLogicException(result.getBusinessErrors());
    }

    /**
     * Convert to uppercase and trim
     * @param value
     * @return
     */
    private static String normalize(String value) {
        return value.trim().toUpperCase(Locale.of("vi", "VN"));
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
