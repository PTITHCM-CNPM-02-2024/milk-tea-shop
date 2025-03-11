package com.mts.backend.shared.value_object;

import java.util.List;

public class ValueObjectValidationResult {
    private IValueObject valueObject;
    private List<String> businessErrors;
    
    public ValueObjectValidationResult(IValueObject valueObject, List<String> businessErrors) {
        this.valueObject = valueObject;
        this.businessErrors = businessErrors;
    }
    
    public IValueObject getValueObject() {
        return valueObject;
    }
    
    public List<String> getBusinessErrors() {
        return businessErrors;
    }
    
    
}
