package com.mts.backend.domain.common.value_object;

import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;

public class BasicName extends AbstractValueObject{
    protected final String value;

    protected BasicName(String value) {
        this.value = value;
    }
    
    
    public String getValue() {
        return value;
    }
    
    
    protected static ValueObjectValidationResult checkNullOrEmpty(String value) {
        List<String> businessErrors = new ArrayList<>();
        if (value == null || value.trim().isEmpty()) {
            businessErrors.add("Tên không được để trống");
        }
        return new ValueObjectValidationResult(new BasicName(value), businessErrors);
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
