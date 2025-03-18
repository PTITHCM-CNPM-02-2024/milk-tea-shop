package com.mts.backend.domain.store.value_object;

import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;

public class MaxTable extends AbstractValueObject {
    private final int value;
    private final static int MAX_TABLE = 100;
    
    private MaxTable(int value) {
        this.value = value;
    }
    
    public static ValueObjectValidationResult create(int value){
        List<String> errors = new ArrayList<>();
        
        if (value < 0 || value > MAX_TABLE){
            errors.add("Số bàn tối đa phải lớn hơn 0 và nhỏ hơn " + MAX_TABLE);
        }
        
        if (errors.isEmpty()){
            return new ValueObjectValidationResult(new MaxTable(value), errors);
        }
        
        return new ValueObjectValidationResult(null, errors);
    }
    
    public int getValue(){
        return value;
    }
    
    public static MaxTable of(int value){
        ValueObjectValidationResult result = create(value);
        
        if (result.getBusinessErrors().isEmpty()){
            return new MaxTable(value);
        }
        throw new IllegalArgumentException("Số bàn tối đa không hợp lệ" + result.getBusinessErrors());
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
    /**
     * @return 
     */
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(value);
    }
}
