package com.mts.backend.domain.customer.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Value
@Builder
public class MemberTypeName  {
    String value;
    private static final int MAX_LENGTH = 50;
    
    private MemberTypeName(String value) {
        Objects.requireNonNull(value, "Member type name is required");
        List<String> errors = new ArrayList<>();

        if (value.length() > MAX_LENGTH) {
            errors.add("Tên loại thành viên không được vượt quá 50 ký tự");
        }

        if (value.isBlank()) {
            errors.add("Tên loại thành viên không được để trống");
        }
        
        if (!errors.isEmpty()) {
            throw new DomainBusinessLogicException(errors);
        }

        this.value = normalizer(value);
    }
    
    
    private static String normalizer(String value) {
        return value.trim().replaceAll("\\s+", " ").toUpperCase();
    }
    
    public static final class MemberTypeNameConverter implements jakarta.persistence.AttributeConverter<MemberTypeName, String> {
        @Override
        public String convertToDatabaseColumn(MemberTypeName attribute) {
            return Objects.isNull(attribute) ? null : attribute.getValue();
        }
        
        @Override
        public MemberTypeName convertToEntityAttribute(String dbData) {
            return Objects.isNull(dbData) ? null : new MemberTypeName(dbData);
        }
    }
    
    
}
