package com.mts.backend.domain.promotion.value_object;

import com.mts.backend.domain.common.value_object.DiscountUnit;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class PromotionDiscountValue {
    @NonNull
    @Getter
    BigDecimal value;
    @NonNull
    @Enumerated(EnumType.STRING)
    @Getter
    DiscountUnit unit;
    @NonNull
    BigDecimal maxDiscountAmount;
    
    public Money getMaxDiscountAmount() {
        return Money.of(maxDiscountAmount);
    }

    public PromotionDiscountValue(BigDecimal value, DiscountUnit unit, Money maxDiscountAmount) {

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

        if (maxDiscountAmount.compareTo(Money.of(BigDecimal.valueOf(1000))) < 0) {
            errors.add("Giá trị giảm giá tối đa không được nhỏ hơn 1000");
        }

        if (unit == DiscountUnit.FIXED && Money.of(value).compareTo(maxDiscountAmount) > 0) {
            errors.add("Giá trị giảm giá không được lớn hơn giá trị giảm giá tối đa");
        }

        if (!errors.isEmpty()) {
            throw new DomainBusinessLogicException(errors);
        }
        this.value = value;
        this.unit = unit;
        this.maxDiscountAmount = maxDiscountAmount.getValue();
    }

    public static PromotionDiscountValueBuilder builder() {
        return new PromotionDiscountValueBuilder();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        PromotionDiscountValue that = (PromotionDiscountValue) o;
        return value != null && Objects.equals(value, that.value)
               && unit != null && Objects.equals(unit, that.unit)
               && maxDiscountAmount != null && Objects.equals(maxDiscountAmount, that.maxDiscountAmount);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(value, unit, maxDiscountAmount);
    }

    public Money maxDiscountAmount() {
        return Money.of(maxDiscountAmount);
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

            if (discountValue.compareTo(Money.of(maxDiscountAmount)) > 0) {
                return Money.of(maxDiscountAmount);
            }
        }

        return totalPrice.subtract(value);

    }


    public static class PromotionDiscountValueBuilder {
        private BigDecimal value;
        private DiscountUnit unit;
        private BigDecimal maxDiscountAmount;

        PromotionDiscountValueBuilder() {
        }

        public PromotionDiscountValueBuilder value(BigDecimal value) {
            this.value = value;
            return this;
        }

        public PromotionDiscountValueBuilder unit(DiscountUnit unit) {
            this.unit = unit;
            return this;
        }

        public PromotionDiscountValueBuilder maxDiscountAmount(@NotNull Money maxDiscountAmount) {
            this.maxDiscountAmount = maxDiscountAmount.getValue();
            return this;
        }

        public PromotionDiscountValue build() {
            return new PromotionDiscountValue(this.value, this.unit, this.maxDiscountAmount);
        }

        public String toString() {
            return "PromotionDiscountValue.PromotionDiscountValueBuilder(value=" + this.value + ", unit=" + this.unit + ", maxDiscountAmount=" + this.maxDiscountAmount + ")";
        }
    }
}
