package com.mts.backend.domain.promotion.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;
import jakarta.persistence.AttributeConverter;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Value@Builder
public class DiscountName {
    String value;
    private final static int MAX_LENGTH = 500;
    
    private DiscountName(String value){
        Objects.requireNonNull(value, "Discount name must not be null");

        List<String> errors = new ArrayList<>();

        if (value.length() > MAX_LENGTH){
            errors.add("Tên khuyến mãi không được quá " + MAX_LENGTH + " ký tự");
        }

        if (value.isBlank() ){
            errors.add("Tên khuyến mãi không được để trống");
        }

        if(! errors.isEmpty()){
            throw new DomainBusinessLogicException(errors);
        }
        this.value = value;
    }
    
    public static final class DiscountNameConverter implements AttributeConverter<DiscountName, String> {
        @Override
        public String convertToDatabaseColumn(DiscountName attribute) {
            return attribute.getValue();
        }
        
        @Override
        public DiscountName convertToEntityAttribute(String dbData) {
            return DiscountName.builder().value(dbData).build();
        }
    }
}
