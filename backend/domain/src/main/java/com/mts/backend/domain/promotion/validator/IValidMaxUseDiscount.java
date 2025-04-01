package com.mts.backend.domain.promotion.validator;


import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
@jakarta.validation.Constraint(validatedBy = {MaxUseDiscountValidator.class})
public @interface IValidMaxUseDiscount {

    String message() default
            "Số lượng sử dụng tối đa không được nhỏ hơn số lượng đã sử dụng";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
