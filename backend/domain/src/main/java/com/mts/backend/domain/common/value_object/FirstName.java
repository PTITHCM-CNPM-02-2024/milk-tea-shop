package com.mts.backend.domain.common.value_object;

import com.mts.backend.shared.exception.DomainException;
import jakarta.persistence.AttributeConverter;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
@Value
@Builder
public class FirstName {

  String value;
    
    private final static Pattern VIETNAMESE_NAME_PATTERN = Pattern.compile("^[A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ][a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]*(?:[ ][A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ][a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]*)*$");

    private FirstName(String value) {
        Objects.requireNonNull(value, "Tên không được để trống");
        List<String> businessErrors = new ArrayList<>();
        if (value.isBlank()) {
            businessErrors.add("Tên không được để trống");
        }
        if (value.length() > 70) {
            businessErrors.add("Tên quá dài (tối đa 70 ký tự)");
        }

        if (!VIETNAMESE_NAME_PATTERN.matcher(value).matches()) {
            // TODO: FIX THIS
            //businessErrors.add("Tên không hợp lệ");
        }
        
        if (!businessErrors.isEmpty()) {
            throw new DomainException("Tên không hợp lệ: " + businessErrors);
        }
        this.value = normalize(value);
    }
    
    
    private String normalize(String value) {
        return value.trim().replaceAll("\\s+", " ").toUpperCase();
    }
    public static final class FirstNameConverter implements AttributeConverter<FirstName, String> {
        @Override
        public String convertToDatabaseColumn(FirstName attribute) {
            return Objects.isNull(attribute) ? null : attribute.getValue();
        }
        
        @Override
        public FirstName convertToEntityAttribute(String dbData) {
            return Objects.isNull(dbData) ? null : new FirstName(dbData);
        }
    }
}
