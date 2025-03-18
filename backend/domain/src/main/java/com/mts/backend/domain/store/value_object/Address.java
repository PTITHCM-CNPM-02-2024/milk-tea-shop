package com.mts.backend.domain.store.value_object;

import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Address extends AbstractValueObject {
    
    private final String value;
    private static final int MAX_LENGTH = 255;
    private Address(String value){
        this.value = value;
    }
    
    public ValueObjectValidationResult create(String value){
        Objects.requireNonNull(value, "Address is required");
        
        List<String> errors = new ArrayList<>();
        
        if (value.length() > MAX_LENGTH){
            errors.add("Địa chỉ không được vượt quá " + MAX_LENGTH + " ký tự");
        }
        
        if (value.isBlank()){
            errors.add("Địa chỉ không được để trống");
        }
        
        if (errors.isEmpty()){
            return new ValueObjectValidationResult(new Address(value), errors);
        }
        
        return new ValueObjectValidationResult(null, errors);
        
    }
    
    public static Address of(String value){
        ValueObjectValidationResult result = new Address(value).create(value);
        
        if (result.getBusinessErrors().isEmpty()){
            return new Address(value);
        }
        throw new IllegalArgumentException("Địa chỉ không hợp lệ" + result.getBusinessErrors());
    }
    
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    public String getValue(){
        return value;
    }
}
