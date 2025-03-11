package com.mts.backend.domain.product.value_object;

import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;

public class ProductSizeName extends AbstractValueObject {
    private final String name;
    
    private static final int MAX_LENGTH = 5;
    private ProductSizeName(String name) {
        this.name = name;
    }
    
    public static ValueObjectValidationResult create(String name) {
        List<String> businessErrors = new ArrayList<>();
        
        if (name == null || name.trim().isEmpty()) {
            businessErrors.add("Tên không được để trống");
        }
        
        if (name != null && name.length() > MAX_LENGTH) {
            businessErrors.add("Tên không được quá " + MAX_LENGTH + " ký tự");
        }
        
        if (businessErrors.isEmpty()) {
            return new ValueObjectValidationResult(new ProductSizeName(name), businessErrors);
        }
        
        return new ValueObjectValidationResult(null, businessErrors);
    }
    
    public static ProductSizeName of(String name) {
        ValueObjectValidationResult result = create(name);
        if (result.getBusinessErrors().isEmpty()) {
            return new ProductSizeName(name);
        }
        throw new IllegalArgumentException("Tên kích thước không hợp lệ: " + result.getBusinessErrors());
    }
    
    private String normalize(String name) {
        return name.toUpperCase().trim();
    }
    
    public String getName() {
        return normalize(name);
    }
    
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(name);
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
}
