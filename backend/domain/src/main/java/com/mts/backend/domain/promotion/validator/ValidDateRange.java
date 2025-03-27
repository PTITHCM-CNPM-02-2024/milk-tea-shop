package com.mts.backend.domain.promotion.validator;

import com.mts.backend.domain.promotion.Discount;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidDateRange implements ConstraintValidator<IValidDateRange, Discount> {
    /**
     * @param value   object to validate 
     * @param context context in which the constraint is evaluated
     * @return
     */
    @Override
    public boolean isValid(Discount value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null values are considered valid
        }
        
        if (value.getValidFrom().isEmpty() || value.getValidUntil() == null) {
            return true; // Null values are considered valid
        }
        
        return value.getValidFrom().get().isBefore(value.getValidUntil()); // Invalid date range
    }
}
