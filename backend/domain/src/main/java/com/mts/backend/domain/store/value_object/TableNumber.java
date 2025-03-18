package com.mts.backend.domain.store.value_object;

import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TableNumber extends AbstractValueObject {
    private final String value;
    private static final int MAX_LENGTH = 10;
    private TableNumber(String value){
        Objects.requireNonNull(value, "Table number is required");
        
        this.value = normalize(value);
    }
    
    public static ValueObjectValidationResult create(String value){
        Objects.requireNonNull(value, "Table number is required");
        List<String> errors = new ArrayList<>();
        
        if (value.length() > MAX_LENGTH){
            errors.add("Số bàn không được vượt quá " + MAX_LENGTH + " ký tự");
        }
        
        if (value.isBlank()){
            errors.add("Số bàn không được để trống");
        }
        
        if (errors.isEmpty()){
            return new ValueObjectValidationResult(new TableNumber(value), errors);
        }
        
        return new ValueObjectValidationResult(null, errors);
    }
    
    public static TableNumber of(String value){
        ValueObjectValidationResult result = create(value);
        
        if (result.getBusinessErrors().isEmpty()){
            return new TableNumber(value);
        }
        throw new DomainException("Mã bàn không hợp lệ" +    result.getBusinessErrors());    
    }
    
    private static String normalize(String value){
        return value.trim().toUpperCase();
    }
    
    public String getValue(){
        return normalize(value);
    }
    
    @Override
    public String toString() {
        return normalize(value);
    }
    /**
     * @return 
     */
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(value);
    }
}
