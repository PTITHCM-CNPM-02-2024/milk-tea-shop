package com.mts.backend.domain.staff.value_object;

import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Position extends AbstractValueObject {
    private final String position;
    private final short MAX_LENGTH = 50;
    private Position(String position) {
        Objects.requireNonNull(position, "Position is required");
        this.position = normalize(position);
    }
    
    public static ValueObjectValidationResult create(String position){
        Objects.requireNonNull(position, "Position is required");
        List<String> errors = new ArrayList<>();
        
        if(position.length() > 50){
            errors.add("Tên chức vụ không được vượt quá 50 ký tự");
        }
        
        if(position.isBlank()){
            errors.add("Tên chức vụ không được để trống");
        }
        
        if(errors.isEmpty()){
            return new ValueObjectValidationResult(new Position(position), errors);
        }
        
        return new ValueObjectValidationResult(null, errors);
    }
    
    public static Position of(String position) {
        ValueObjectValidationResult result = create(position);
        
        if(result.getBusinessErrors().isEmpty()){
            return new Position(position);
        }
        
        throw new IllegalArgumentException("Tên chức vụ không hợp lệ: " + result.getBusinessErrors());
        
    }
    
    private static String normalize(String position) {
        return position.trim().toUpperCase();
    }
    /**
     * @return 
     */
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(position);
    }
    
    public String getValue() {
        return normalize(position);
    }
    
    @Override
    public String toString() {
        return position;
    }
}
