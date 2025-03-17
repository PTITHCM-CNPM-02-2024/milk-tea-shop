package com.mts.backend.domain.customer.value_object;

import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class DiscountValue extends AbstractValueObject {
    private final BigDecimal value;
    private final DiscountUnit unit;
    private DiscountValue(BigDecimal value, DiscountUnit unit){
        this.value = value.setScale(3, RoundingMode.HALF_UP);
        this.unit = unit;
    }
    
    public static ValueObjectValidationResult create(BigDecimal value, DiscountUnit unit){
        List<String> errors = new ArrayList<>();
        
        if(value == null){
            errors.add("Giá trị giảm giá không được để trống");
        }
        
        if(value != null && value.compareTo(BigDecimal.ZERO) < 0){
            errors.add("Giá trị giảm giá không được nhỏ hơn 0");
        }
        
        if(unit == null){
            errors.add("Đơn vị giảm giá không được để trống");
        }
        
        if (unit != null && unit.ordinal() == 1 && value != null && value.compareTo(BigDecimal.valueOf(100)) > 0) {
            errors.add("Giá trị giảm giá không được lớn hơn 100 khi đơn vị là phần trăm");
        }
        
        if (errors.isEmpty()) {
            return new ValueObjectValidationResult(new DiscountValue(value, unit), errors);
        }
        
        return new ValueObjectValidationResult(null, errors);
    }
    
    public static DiscountValue of(BigDecimal value, DiscountUnit unit){
        ValueObjectValidationResult result = create(value, unit);
        
        if(result.getBusinessErrors().isEmpty()){
            return new DiscountValue(value, unit);
        }
        
        throw new IllegalArgumentException("Giá trị giảm giá không hợp lệ: " + result.getBusinessErrors());
    }
    
    public BigDecimal getValue() {
        return value;
    }
    
    public DiscountUnit getUnit() {
        return unit;
    }
    
    public boolean isPercentage() {
        return unit == DiscountUnit.PERCENTAGE;
    }
    
    public boolean isMoney() {
        return unit == DiscountUnit.FIXED;
    }
    
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(value, unit);
    }
    
    @Override
    public String toString() {
        return value + " " + unit;
    }
    
    
    
    
}
