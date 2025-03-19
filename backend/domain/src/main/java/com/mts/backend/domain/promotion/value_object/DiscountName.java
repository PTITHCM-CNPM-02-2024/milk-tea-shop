package com.mts.backend.domain.promotion.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DiscountName extends AbstractValueObject {
    private final  String value;
    private final static int MAX_LENGTH = 500;
    
    private DiscountName(String value){
        this.value = value;
    }
    
    public static ValueObjectValidationResult create(String value){
        Objects.requireNonNull(value, "Discount name must not be null");
        
        List<String> errors = new ArrayList<>();
        
        if (value.length() > MAX_LENGTH){
            errors.add("Tên khuyến mãi không được quá " + MAX_LENGTH + " ký tự");
        }
        
        if (value.isBlank() ){
            errors.add("Tên khuyến mãi không được để trống");
        }
        
        if( errors.isEmpty()){
            return new ValueObjectValidationResult(new DiscountName(value), errors);
        }
        
        return new ValueObjectValidationResult(null, errors);
        
        
    }
    
    public static DiscountName of(String value){
        ValueObjectValidationResult result = create(value);
        
        if (result.getBusinessErrors().isEmpty()){
            return new DiscountName(value);
        }
        
        throw new DomainBusinessLogicException(result.getBusinessErrors());
    }
    
    public String getValue() {
        return value;
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
