package com.mts.backend.domain.promotion.validator;


import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
@jakarta.validation.Constraint(validatedBy = {MaxUseDiscountValidator.class})
public @interface IValidMaxUseDiscount {
}
