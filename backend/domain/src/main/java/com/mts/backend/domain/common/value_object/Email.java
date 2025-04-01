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
public class Email{
    
  String value;
    
    private final static Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    
    private Email(String value){
        Objects.requireNonNull(value, "Email is required");
        List<String> errors = new ArrayList<>();

        if (value.isBlank()) {
            errors.add("Email không được để trống");
        }

        if (value.length() > 255) {
            errors.add("Email quá dài (tối đa 255 ký tự)");
        }

        if (!EMAIL_PATTERN.matcher(value).matches()) {
            errors.add("Email không hợp lệ");
        }

        if (!errors.isEmpty()) {
            throw new DomainBusinessLogicException(errors);
        }
        this.value = value;
    }
    public static final class EmailConverter implements AttributeConverter<Email, String> {
        @Override
        public String convertToDatabaseColumn(Email attribute) {
            return Objects.isNull(attribute) ? null : attribute.getValue();
        }
        
        @Override
        public Email convertToEntityAttribute(String dbData) {
            return Objects.isNull(dbData) ? null : Email.builder().value(dbData).build();
        }
    }
}
