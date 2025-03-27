package com.mts.backend.domain.promotion.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
@Constraint(validatedBy = ValidDateRange.class)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IValidDateRange {
    String message() default "Thời gian bắt đầu phải nhỏ hơn thời gian kết thúc";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
