package com.mts.backend.domain.store.value_object;

import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AreaName extends AbstractValueObject {
    private final String value;
    private static final int MAX_LENGTH = 3;
    private AreaName(String value){
        this.value = normalize(value);
    }
    
    private static String normalize(String value){
        return value.trim().toUpperCase();
    }
    
    public static ValueObjectValidationResult create(String value){
        Objects.requireNonNull(value, "Area name is required");
        
        List<String> errors = new ArrayList<>();
        
        if (value.length() != MAX_LENGTH){
            errors.add("Tên khu vực phải có " + MAX_LENGTH + " ký tự");
        }
        
        if (errors.isEmpty()){
            return new ValueObjectValidationResult(new AreaName(value), errors);
        }
        
        return new ValueObjectValidationResult(null, errors);
    } 
    /**
     * @return 
     */
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(value);
    }
    
    public String getValue(){
        return value;
    }
    
    public static AreaName of(String value){
        ValueObjectValidationResult result = create(value);
        
        if (result.getBusinessErrors().isEmpty()){
            return new AreaName(value);
        }
        throw new DomainException("Tên khu vực không hợp lệ" + result.getBusinessErrors());
    }
    
    @Override
    public String toString() {
        return normalize(value);
    }
    
    
    
    
}
