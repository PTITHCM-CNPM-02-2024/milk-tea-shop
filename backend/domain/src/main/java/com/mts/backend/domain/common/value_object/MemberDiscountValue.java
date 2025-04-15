package com.mts.backend.domain.common.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Builder
@Embeddable
@NoArgsConstructor(force = true)
@Data
@EqualsAndHashCode
public class MemberDiscountValue {
    @NotNull
    BigDecimal value;
    @NotNull
    @Enumerated(EnumType.STRING)
    DiscountUnit unit;

    private MemberDiscountValue(BigDecimal value, DiscountUnit unit) {
        List<String> errors = new ArrayList<>();

        if (value == null) {
            errors.add("Giá trị giảm giá không được để trống");
        }

        if (value != null && value.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Giá trị giảm giá không được nhỏ hơn 0");
        }

        if (unit == null) {
            errors.add("Đơn vị giảm giá không được để trống");
        }

        if (unit != null && unit.ordinal() == 1 && value != null && value.compareTo(BigDecimal.valueOf(100)) > 0) {
            errors.add("Giá trị giảm giá không được lớn hơn 100 khi đơn vị là phần trăm");
        }

        if (unit != null && unit.ordinal() == 0 && value != null && value.compareTo(BigDecimal.valueOf(1000)) < 0) {
            errors.add("Giá trị giảm giá không được nhỏ hơn 1000 khi đơn vị là tiền");
        }


        if (!errors.isEmpty()) {
            throw new DomainBusinessLogicException(errors);
        }


        this.value = value.setScale(3, RoundingMode.HALF_UP);
        this.unit = unit;
    }
    @Transient
    public boolean isPercentage() {
        return unit == DiscountUnit.PERCENTAGE;
    }

    @Transient
    public boolean isMoney() {
        return unit == DiscountUnit.FIXED;
    }
    
    public String getDescription() {
        if (isPercentage()) {
            return "Giảm giá " + value + "%";
        } else if (isMoney()) {
            return "Giảm giá " + value + " VNĐ";
        }
        return "";
    }


}
