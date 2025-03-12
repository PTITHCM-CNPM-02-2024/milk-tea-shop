package com.mts.backend.domain.product.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;

public class UnitSymbol extends AbstractValueObject {
    private final String value;
    
    private static final int MAX_LENGTH = 5;
    
    private UnitSymbol(String value) {
        this.value = value;
    }
    
    public static ValueObjectValidationResult create(String value) {
        List<String> businessErrors = new ArrayList<>();
        
        if (value == null || value.trim().isEmpty()) {
            businessErrors.add("Ký hiệu đơn vị đo không được để trống");
        }
        
        if (value != null && value.length() > MAX_LENGTH) {
            businessErrors.add("Ký hiệu đơn vị đo không được quá " + MAX_LENGTH + " ký tự");
        }
        
        if (!businessErrors.isEmpty()) {
            return new ValueObjectValidationResult(null, businessErrors);
        }
        
        return new ValueObjectValidationResult(new UnitSymbol(value), businessErrors);
    }
    /**
     * @return 
     */
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(value);
    }
    
    private String normalize(String value) {
        return value.trim();
    }
    
    public String getValue() {
        return normalize(value);
    }
    
    public static UnitSymbol of(String value) {
        ValueObjectValidationResult validationResult = create(value);
        if (!validationResult.getBusinessErrors().isEmpty()) {
            throw new DomainBusinessLogicException(validationResult.getBusinessErrors());
        }
        return new UnitSymbol(value);
    }
    
    @Override
    public String toString() {
        return normalize(value);
    }
    
    
}
