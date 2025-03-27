package com.mts.backend.domain.common.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.AttributeConverter;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Value
@Builder
public class PhoneNumber {

    private final static Pattern PHONE_NUMBER_PATTERN = Pattern.compile("(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)");
    String value;

    private PhoneNumber(String value) {
        Objects.requireNonNull(value, "Phone number is required");

        List<String> errors = new ArrayList<>();

        if (value.isBlank()) {
            errors.add("Số điện thoại không được để trống");
        }

        if (!PHONE_NUMBER_PATTERN.matcher(value).matches()) {
            errors.add("Số điện thoại không hợp lệ");
        }

        if (!errors.isEmpty()) {
            throw new DomainBusinessLogicException(errors);
        }
        this.value = normalize(value);
    }

    private static String normalize(String value) {
        return value.replaceAll("[^0-9]", "");
    }
    
    public static final class PhoneNumberConverter implements AttributeConverter<PhoneNumber, String> {
        @Override
        public String convertToDatabaseColumn(PhoneNumber attribute) {
            return Objects.requireNonNull(attribute).getValue();
        }
    
        @Override
        public PhoneNumber convertToEntityAttribute(String dbData) {
            return builder().value(dbData).build();
        }
    }


}
