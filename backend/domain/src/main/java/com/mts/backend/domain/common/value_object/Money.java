package com.mts.backend.domain.common.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import com.mts.backend.shared.exception.DomainException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Value(staticConstructor = "of")
public class Money  implements Comparable<Money>, Serializable {
    BigDecimal value;
    private static final int DEFAULT_SCALE = 3;
    
    public Money(BigDecimal value) {
        Objects.requireNonNull(value, "BigDecimal is required");
        List<String> businessErrors = new ArrayList<>(List.of());


//        // Allow zero amounts, only validate positive values
//        if (value.compareTo(BigDecimal.ZERO) > 0 && value.compareTo(BigDecimal.valueOf(1000)) < 0) {
//            businessErrors.add("Đơn vị tiền là VNĐ, số tiền phải bằng 0 hoặc lớn hơn 1000");
//        }
        
        if (!businessErrors.isEmpty()) {
            throw new DomainBusinessLogicException(businessErrors);
        }
        this.value = value.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
    }
    
    
    public Money add(Money money){
        return new Money(value.add(money.value));
    }
    
    public Money add(BigDecimal money){
        return new Money(value.add(money));
    }
    
    public Money subtract(Money money){
        return new Money(value.subtract(money.value).compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO :
                value.subtract(money.value));
    }
    
    public Money subtract(BigDecimal money){
        return new Money(value.subtract(money).doubleValue() < 0 ? BigDecimal.ZERO : value.subtract(money));
    }
    
    
    public Money multiply(Money value){
        return new Money(this.value.multiply(value.value));
    }
    
    public Money multiply(BigDecimal value){
        return new Money(this.value.multiply(value));
    }
    
    public Money divide(Money value){
        if (value.value.equals(Money.ZERO.getValue())) {
            throw new DomainException("Không thể chia cho 0");
        }
        BigDecimal newValue = this.value.divide(value.getValue(), DEFAULT_SCALE, RoundingMode.HALF_UP);
        
        return new Money(newValue);
    }
    
    public Money divide(BigDecimal value){
        if (value.equals(BigDecimal.ZERO)) {
            throw new DomainException("Không thể chia cho 0");
        }
        BigDecimal newValue = this.value.divide(value, DEFAULT_SCALE, RoundingMode.HALF_UP);
        
        return new Money(newValue);
    }
    
    
    
    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money discount(double percentageOff) {
        if (percentageOff < 0 || percentageOff > 100) {
            throw new DomainException("Phần trăm giảm giá phải nằm trong khoảng từ 0 đến 100");
        }

        // Convert percentage to decimal for calculation (e.g., 20% -> 0.2)
        BigDecimal discountRate = BigDecimal.valueOf(percentageOff)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        // Calculate discount amount = original amount * discount rate
        BigDecimal discountAmount = value.multiply(discountRate);

        // Subtract discount from original amount
        BigDecimal finalAmount = value.subtract(discountAmount);

        return new Money(finalAmount);
    }
    

    /**
     * @param o the object to be compared. 
     * @return
     */
    @Override
    public int compareTo(Money o) {
        return value.compareTo(o.value);
    }

}
