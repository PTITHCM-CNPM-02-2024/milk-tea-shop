package com.mts.backend.domain.promotion.value_object;

import com.mts.backend.domain.common.value_object.DiscountUnit;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Value
@Embeddable
@NoArgsConstructor(force = true)
@Builder
public class PromotionDiscountValue {
    @NonNull
    BigDecimal value;
    @NonNull
    @Enumerated(EnumType.STRING)
    DiscountUnit unit;
    @NonNull
    @Convert(converter = Money.MoneyConverter.class)
    Money maxDiscountAmount;


    private PromotionDiscountValue(BigDecimal value, DiscountUnit unit, Money maxDiscountAmount) {

        Objects.requireNonNull(value, "Giá trị giảm giá không được để trống");
        Objects.requireNonNull(unit, "Đơn vị giảm giá không được để trống");
        Objects.requireNonNull(maxDiscountAmount, "Giá trị giảm giá tối đa không được để trống");

        List<String> errors = new ArrayList<>();

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Giá trị giảm giá không được nhỏ hơn 0");
        }

        if (unit.ordinal() == 1 && value.compareTo(BigDecimal.valueOf(100)) > 0) {
            errors.add("Giá trị giảm giá không được lớn hơn 100 khi đơn vị là phần trăm");
        }

        if (unit.ordinal() == 0 && value.compareTo(BigDecimal.valueOf(1000)) < 0) {
            errors.add("Giá trị giảm giá không được nhỏ hơn 1000 khi đơn vị là tiền");
        }

        if (maxDiscountAmount.compareTo(Money.builder().value(BigDecimal.valueOf(1000)).build()) < 0) {
            errors.add("Giá trị giảm giá tối đa không được nhỏ hơn 1000");
        }

        if (unit == DiscountUnit.FIXED && value.compareTo(maxDiscountAmount.getValue()) > 0) {
            errors.add("Giá trị giảm giá không được lớn hơn giá trị giảm giá tối đa");
        }
        
        if (!errors.isEmpty()) {
            throw new DomainBusinessLogicException(errors);
        }
        this.value = value;
        this.unit = unit;
        this.maxDiscountAmount = maxDiscountAmount;
    }
    
    public boolean isPercentage() {
        return unit == DiscountUnit.PERCENTAGE;
    }
    
    public boolean isFixed() {
        return unit == DiscountUnit.FIXED;
    }

    public String getDescription() {
        if (isPercentage()) {
            return value + "% giảm giá tối đa " + maxDiscountAmount;
        }

        return "Giảm " + maxDiscountAmount + " cho mỗi đơn hàng";
    }
    
    public Money getDiscountValue(Money totalPrice) {
        if (isPercentage()) {
            var discountValue = totalPrice.multiply(value).divide(BigDecimal.valueOf(100));
            
            if (discountValue.compareTo(maxDiscountAmount) > 0) {
                return maxDiscountAmount;
            }
        }
        
        return totalPrice.subtract(value);
        
    }
    
    

}
