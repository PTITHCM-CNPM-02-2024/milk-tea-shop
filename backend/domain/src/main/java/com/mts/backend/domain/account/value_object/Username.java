package com.mts.backend.domain.account.value_object;

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
public class Username  {
    
    String value;
    
    private final static Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,20}$");
    
    private Username(String value) {
        Objects.requireNonNull(value, "Username is required");
        Objects.requireNonNull(value, "Username is required");
        List<String> errors = new ArrayList<>();
        if (value.isBlank()) {
            errors.add("tên đăng nhập không được để trống");
        }
        if (value.length() < 3) {
            errors.add("tên đăng nhập quá ngắn");
        }
        if (value.length() > 20) {
            errors.add("tên đăng nhập quá dài");
        }
        if (!USERNAME_PATTERN.matcher(value).matches()) {
            errors.add("tên đăng nhập không hợp lệ");
        }
        
        if (!errors.isEmpty()) {
            throw new DomainBusinessLogicException(errors);
        }
        this.value = value;
    }
    
    public static final class UsernameConverter implements AttributeConverter<Username, String> {
        @Override
        public String convertToDatabaseColumn(Username attribute) {
            return Objects.isNull(attribute) ? null : attribute.getValue();
        }
        
        @Override
        public Username convertToEntityAttribute(String dbData) {
            return Objects.isNull(dbData) ? null : Username.builder().value(dbData).build();
        }
    }
}
