package com.mts.backend.domain.promotion.value_object;

import com.mts.backend.domain.common.value_object.DiscountUnit;
import com.mts.backend.domain.common.value_object.MemberDiscountValue;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PromotionDiscountValue extends AbstractValueObject {
    @NonNull
    private final BigDecimal value;
    @NonNull
    private final DiscountUnit unit;
    @NonNull
    private final Money maxDiscountValue;
    
    
    private PromotionDiscountValue(BigDecimal value, DiscountUnit unit, Money maxDiscountValue){
        this.value = value;
        this.unit = unit;
        this.maxDiscountValue = maxDiscountValue;
    }
    
    public static ValueObjectValidationResult create(BigDecimal value, DiscountUnit unit, Money maxDiscountValue){

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

        if (unit != null && unit.ordinal() == 0 && value != null && value.compareTo(BigDecimal.valueOf(1000)) < 0) {
            errors.add("Giá trị giảm giá không được nhỏ hơn 1000 khi đơn vị là tiền");
        }
        
        if (maxDiscountValue == null) {
            errors.add("Giá trị giảm giá tối đa không được để trống");
        }
        
        if (maxDiscountValue != null && maxDiscountValue.compareTo(Money.of(BigDecimal.valueOf(1000L))) < 0) {
            errors.add("Giá trị giảm giá tối đa không được nhỏ hơn 1000");
        }
        
        if (maxDiscountValue != null && unit == DiscountUnit.FIXED && value != null && value.compareTo(maxDiscountValue.getAmount()) > 0) {
            errors.add("Giá trị giảm giá không được lớn hơn giá trị giảm giá tối đa");
        }
        
        if (errors.isEmpty()) {
            return new ValueObjectValidationResult(new PromotionDiscountValue(value, unit, maxDiscountValue), errors);
        }
        
        return new ValueObjectValidationResult(null, errors);
        
    }
    
    public static PromotionDiscountValue of(BigDecimal value, DiscountUnit unit, Money maxDiscountValue){
        ValueObjectValidationResult result = create(value, unit, maxDiscountValue);
        
        if(result.getBusinessErrors().isEmpty()){
            return new PromotionDiscountValue(value, unit, maxDiscountValue);
        }
        
        throw new IllegalArgumentException("Giá trị giảm giá không hợp lệ: " + result.getBusinessErrors());
    }
    
    public BigDecimal getValue() {
        return value;
    }
    
    public DiscountUnit getUnit() {
        return unit;
    }
    
    public Money getMaxDiscountValue() {
        return maxDiscountValue;
    }
    
    /**
     * @return 
     */
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(value, unit, maxDiscountValue);
    }
    
    public boolean isPercentage() {
        return unit == DiscountUnit.PERCENTAGE;
    }
    
    public boolean isMoney() {
        return unit == DiscountUnit.FIXED;
    }
    
    @Override
    public String toString() {
        return "PromotionDiscountValue{" +
                "value=" + value +
                ", unit=" + unit +
                ", maxDiscountValue=" + maxDiscountValue +
                '}';
    }
    
    public String getDescription() {
        if (isPercentage()) {
            return value + "% giảm giá tối đa " + maxDiscountValue;
        }
        
        return "Giảm " + maxDiscountValue + " cho mỗi đơn hàng";
    }
    
    
}
