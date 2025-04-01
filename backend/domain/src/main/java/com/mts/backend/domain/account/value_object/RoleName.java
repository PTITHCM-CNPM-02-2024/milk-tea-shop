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
public class RoleName {
    
String value;
    
    private final static Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    
    private RoleName(String value) {
        Objects.requireNonNull(value, "Role name is required");
        List<String> businessErrors = new ArrayList<>();

        if (value.isBlank()) {
            businessErrors.add("Tên vai trò không được để trống");
        }

        if (value.length() < 3 || value.length() > 20) {
            businessErrors.add("Tên vai trò phải từ 3 đến 20 ký tự");
        }

        if (!pattern.matcher(value).matches()) {
            businessErrors.add("Tên vai trò không hợp lệ");
        }

        if (!businessErrors.isEmpty()) {
            throw new DomainBusinessLogicException(businessErrors);
        }
        this.value = value;
    }
    
    private static String normalize(String value) {
        return value.trim().replaceAll("\\s+", " ").toLowerCase();
    }
    @jakarta.persistence.Converter(autoApply = true)
    public static final class RoleNameConverter implements AttributeConverter<RoleName, String> {
        @Override
        public String convertToDatabaseColumn(RoleName attribute) {
            return Objects.isNull(attribute) ? null : attribute.getValue();
        }
        
        @Override
        public RoleName convertToEntityAttribute(String dbData) {
            return Objects.isNull(dbData) ? null : RoleName.builder().value(dbData).build();
        }
    }
}
