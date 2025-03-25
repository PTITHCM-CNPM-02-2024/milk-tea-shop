package com.mts.backend.domain.common.value_object;

import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Money extends AbstractValueObject implements Comparable<Money> {
    private final BigDecimal amount;
    private static final int DEFAULT_SCALE = 3;
    
    private Money(BigDecimal amount) {
        this.amount = amount.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
    }
    
    public static ValueObjectValidationResult create(BigDecimal amount) {
        Objects.requireNonNull(amount, "BigDecimal is required");
        List<String> businessErrors = new ArrayList<>(List.of());
        
        if ( amount.doubleValue() < 1000) {
            businessErrors.add("Đơn vị tiền là VNĐ, số tiền phải lớn hơn 1000");
        }

        if (businessErrors.isEmpty()) {
            return new ValueObjectValidationResult(new Money(amount), businessErrors);
        }
        return new ValueObjectValidationResult(null, businessErrors);
    }
    
    public static ValueObjectValidationResult create(double amount) {
        return create(BigDecimal.valueOf(amount));
    }
    
    public static Money of(BigDecimal amount) {
        ValueObjectValidationResult result = create(amount);
        if (result.getBusinessErrors().isEmpty()) {
            return new Money(amount);
        }
        throw new IllegalArgumentException("Giá tiền không chính xác: " + result.getBusinessErrors());
    }
    
    public Money add(Money money){
        return new Money(amount.add(money.amount));
    }
    
    public Money subtract(Money money){
        return new Money(amount.subtract(money.amount));
    }
    
    
    public Money multiply(BigDecimal value){
        
        if (value.doubleValue() < 0) {
            throw new IllegalArgumentException("Số lượng không hợp lệ");
        }
        BigDecimal newValue = amount.multiply(value);
        return new Money(newValue);
    }
    
    public Money divide(BigDecimal value){
        if (value.doubleValue() <= 0) {
            throw new IllegalArgumentException("Số lượng không hợp lệ");
        }
        BigDecimal newValue = amount.divide(value, DEFAULT_SCALE, RoundingMode.HALF_UP);
        return new Money(newValue);
    }
    
    public static final Money ZERO = new Money(BigDecimal.ZERO);
    
    public Money discount(double percentageOff){
        if(percentageOff < 0 || percentageOff > 100){
            throw new IllegalArgumentException("Phần trăm giảm giá không hợp lệ");
        }
        
        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(BigDecimal.valueOf(percentageOff).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));
        
        BigDecimal discountedAmount = amount.multiply(discountMultiplier);
        
        return new Money(discountedAmount);
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public double getValue() {
        return amount.doubleValue();
    }
    
    
    @Override
    public String toString() {
        return amount.toString();
    }


    /**
     * @return 
     */
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(amount);
    }

    /**
     * @param o the object to be compared. 
     * @return
     */
    @Override
    public int compareTo(Money o) {
        return amount.compareTo(o.amount);
    }
}
