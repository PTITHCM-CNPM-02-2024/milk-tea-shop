package com.mts.backend.domain.store.value_object;

import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StoreName extends AbstractValueObject  {
    private final String value;
    
    private static final int MAX_LENGTH = 100;
    private StoreName(String value) {
        this.value = normalize(value);
    }
    
    public static ValueObjectValidationResult create(String value){
        Objects.requireNonNull(value, "Store name is required");
        
        List<String> errors = new ArrayList<>();
        
        if (value.length() > MAX_LENGTH){
            errors.add("Tên cửa hàng không được vượt quá " + MAX_LENGTH + " ký tự");
        }
        
        if (value.isBlank()){
            errors.add("Tên cửa hàng không được để trống");
        }
        
        if (errors.isEmpty()){
            return new ValueObjectValidationResult(new StoreName(value), errors);
        }
        
        return new ValueObjectValidationResult(null, errors);
    }
    
    public static StoreName of(String value){
        ValueObjectValidationResult result = create(value);
        
        if (result.getBusinessErrors().isEmpty()){
            return new StoreName(value);
        }
        throw new DomainException("Tên cửa hàng không hợp lệ" + result.getBusinessErrors());
    }
    
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(value);
    }
    
    @Override
    public String toString() {
        return normalize(value);
    }

    public String getValue() {
        return normalize(value);
    }
    
    private static String normalize(String value){
        return value.trim().toUpperCase();
    }
}
