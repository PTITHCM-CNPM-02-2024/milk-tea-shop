package com.mts.backend.domain.common.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
@Value
@Builder
public class LastName {
  String value;

    private final static Pattern VIETNAMESE_NAME_PATTERN = Pattern.compile("^[A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ][a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]*(?:[ ][A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ][a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]*)*$");    
    private LastName(String value) {
        Objects.requireNonNull(value, "Họ không được để trống");

        List<String> businessErrors = new ArrayList<>();

        if (value.isBlank()) {
            businessErrors.add("Họ không được để trống");
        }

        if (value.length() > 70) {
            businessErrors.add("Họ quá dài (tối đa 70 ký tự)");
        }

        if (!VIETNAMESE_NAME_PATTERN.matcher(value).matches()) {
            // TODO: FIX THIS
            //businessErrors.add("Họ không hợp lệ");
        }

        if (!businessErrors.isEmpty()) {
            throw new DomainBusinessLogicException(businessErrors);
        }
        this.value = normalize(value);
    }
    
    /**
     * @param value 
     * @return 
     */
    private static String normalize(String value) {
        return value.trim().replaceAll("\\s+", " ").toUpperCase();
    }
    public static final class LastNameConverter implements jakarta.persistence.AttributeConverter<LastName, String> {
        @Override
        public String convertToDatabaseColumn(LastName attribute) {
            return Objects.isNull(attribute) ? null : attribute.getValue();
        }
        
        @Override
        public LastName convertToEntityAttribute(String dbData) {
            return Objects.isNull(dbData) ? null : new LastName(dbData);
        }
    }
}
