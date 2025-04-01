package com.mts.backend.domain.promotion.validator;

import com.mts.backend.domain.promotion.DiscountEntity;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MaxUseDiscountValidator implements ConstraintValidator<IValidMaxUseDiscount, DiscountEntity> {
    /**
     * @param value   object to validate 
     * @param context context in which the constraint is evaluated
     * @return
     */
    @Override
    public boolean isValid(DiscountEntity value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null values are considered valid
        }

        if (value.getMaxUse().isEmpty() || value.getMaxUsesPerCustomer().isEmpty()) {
            return true; // Null values are considered valid
        }

        return value.getMaxUse().get() >= value.getMaxUsesPerCustomer().get(); // Invalid max use
    }
}
