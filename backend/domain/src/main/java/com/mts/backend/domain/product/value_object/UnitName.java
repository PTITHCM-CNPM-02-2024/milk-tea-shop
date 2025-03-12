package com.mts.backend.domain.product.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;

public class UnitName extends AbstractValueObject {
    
    private final String name;
    
    private static final int MAX_LENGTH = 10;
    
    private UnitName(String name) {
        this.name = name;
    }
    
    public static ValueObjectValidationResult create(String name) {

        List<String> businessErrors = new ArrayList<>();
        if (name == null || name.trim().isEmpty()) {
            businessErrors.add("Tên đơn vị đo không được để trống");
        }
        
        if (name != null && name.length() > MAX_LENGTH) {
            businessErrors.add("Tên đơn vị đo không được quá " + MAX_LENGTH + " ký tự");
        }
        
        if(!businessErrors.isEmpty()) {
            return new ValueObjectValidationResult(null, businessErrors);
        }
        
        return new ValueObjectValidationResult(new UnitName(name), businessErrors);
        
    }
    
    public static UnitName of(String name) {
        ValueObjectValidationResult validationResult = create(name);
        if (!validationResult.getBusinessErrors().isEmpty()) {
            throw new DomainBusinessLogicException(validationResult.getBusinessErrors());
        }
        return new UnitName(name);
    }
    /**
     * @return 
     */
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(name);
    }
    
    private String normalize(String name) {
        return name.trim();
    }
    
    public String getValue() {
        return normalize(name);
    }
    
    @Override
    public String toString() {
        return normalize(name);
    }
    
}
