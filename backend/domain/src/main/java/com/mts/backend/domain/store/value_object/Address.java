package com.mts.backend.domain.store.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;
import jakarta.persistence.AttributeConverter;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Value
@Builder
public class Address {
    
String value;
    private static final int MAX_LENGTH = 255;
    private Address(String value){

        Objects.requireNonNull(value, "Address is required");

        List<String> errors = new ArrayList<>();

        if (value.length() > MAX_LENGTH){
            errors.add("Địa chỉ không được vượt quá " + MAX_LENGTH + " ký tự");
        }

        if (value.isBlank()){
            errors.add("Địa chỉ không được để trống");
        }
        
        if (!errors.isEmpty()){
            throw new DomainBusinessLogicException(errors);
        }
        this.value = value;
    }
    
    public static final class AddressConverter implements AttributeConverter<Address, String> {
        @Override
        public String convertToDatabaseColumn(Address attribute) {
            return Objects.isNull(attribute) ? null : attribute.getValue();
        }
    
        @Override
        public Address convertToEntityAttribute(String dbData) {
            return Objects.isNull(dbData) ? null : Address.builder().value(dbData).build();
        }
    }
}
