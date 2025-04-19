package com.mts.backend.domain.promotion.value_object;


import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.AttributeConverter;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
@Value(staticConstructor = "of")
public class CouponCode {
    @jakarta.validation.constraints.Pattern(regexp="^[a-zA-Z0-9]{3,15}$",
            message="Mã coupon phải chứa ít nhất 3 ký tự và không quá 15 ký tự")
    @jakarta.validation.constraints.NotBlank(message = "Mã coupon không được để trống")
    String value;
    private CouponCode(String value){
        this.value = normalize(value);
    }
    
    
    private static String normalize(String value){
        return value.trim().toUpperCase();
    }
    
}
