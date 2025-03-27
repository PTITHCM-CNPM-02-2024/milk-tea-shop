package com.mts.backend.domain.promotion.value_object;


import com.mts.backend.shared.exception.DomainBusinessLogicException;
import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;
import jakarta.persistence.AttributeConverter;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
@Value
@Builder
public class CouponCode {
    String value;
    private final static Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{3,15}$");
    private CouponCode(String value){
        Objects.requireNonNull(value, "Coupon code must not be null");

        List<String> errors = new ArrayList<>();

        if (!pattern.matcher(value).matches()){
            errors.add("Mã coupon phải chứa ít nhất 3 ký tự và không quá 15 ký tự");
        }

        if(! errors.isEmpty()){
            throw new DomainBusinessLogicException(errors);
        }
        this.value = normalize(value);
    }
    
    
    private static String normalize(String value){
        return value.trim().toUpperCase();
    }
    
    public static final class CouponCodeConverter implements AttributeConverter<CouponCode, String> {
        @Override
        public String convertToDatabaseColumn(CouponCode attribute) {
            return Objects.isNull(attribute) ? null : attribute.getValue();
        }
        
        @Override
        public CouponCode convertToEntityAttribute(String dbData) {
            return Objects.isNull(dbData) ? null : CouponCode.builder().value(dbData).build();
        }
    }
    
}
