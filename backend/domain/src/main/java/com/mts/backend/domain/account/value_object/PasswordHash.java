package com.mts.backend.domain.account.value_object;

import com.mts.backend.shared.exception.DomainException;
import jakarta.persistence.AttributeConverter;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

@Value
@Builder
public class PasswordHash {

    String value;

    private PasswordHash(String value) {
        if (value.isBlank()) {
            throw new DomainException("Mật khẩu không được để trống");
        }
        this.value = value;
    }
    
    
    public static final class PasswordHashConverter implements AttributeConverter<PasswordHash, String> {
        @Override
        public String convertToDatabaseColumn(PasswordHash attribute) {
            return Objects.isNull(attribute) ? null : attribute.getValue();
        }

        @Override
        public PasswordHash convertToEntityAttribute(String dbData) {
            return Objects.isNull(dbData) ? null : PasswordHash.builder().value(dbData).build();
        }
    }
}
