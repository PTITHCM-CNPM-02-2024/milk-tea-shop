package com.mts.backend.domain.product.value_object;

import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;

public class QuantityOfProductSize extends AbstractValueObject {
    private final int quantity;
    
    private QuantityOfProductSize(int quantity) {
        this.quantity = quantity;
    }
    
    public int getValue() {
        return quantity;
    }
    
    public static ValueObjectValidationResult checkValidQuantity(int quantity) {
        List<String> businessErrors = new ArrayList<>();
        if (quantity <= 0) {
            businessErrors.add("Số lượng phải lớn hơn 0");
        }
        
        if (businessErrors.isEmpty()) {
            return new ValueObjectValidationResult(new QuantityOfProductSize(quantity), businessErrors);
        }
        
        return new ValueObjectValidationResult(null, businessErrors);
    }
    /**
     * @return 
     */
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(quantity);
    }
    
    public static QuantityOfProductSize of(int quantity) {
        ValueObjectValidationResult result = checkValidQuantity(quantity);
        if (result.getBusinessErrors().isEmpty()) {
            return new QuantityOfProductSize(quantity);
        }
        throw new IllegalArgumentException("Số lượng không hợp lệ: " + result.getBusinessErrors());
    }
    
    @Override
    public String toString() {
        return String.valueOf(quantity);
    }
    
    
    
    
}
