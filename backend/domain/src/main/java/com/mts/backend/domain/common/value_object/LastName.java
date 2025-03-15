package com.mts.backend.domain.common.value_object;

import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class LastName extends AbstractValueObject {
    
    private final String value;

    private final static Pattern VIETNAMESE_NAME_PATTERN = Pattern.compile("^[A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ][a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]*(?:[ ][A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ][a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]*)*$");    
    private LastName(String value) {
        this.value = normalize(value);
    }
    
    public static ValueObjectValidationResult create(String value) {
        Objects.requireNonNull(value, "Họ không được để trống");
        
        List<String> businessErrors = new ArrayList<>();
        
        if (value.isBlank()) {
            businessErrors.add("Họ không được để trống");
        }
        
        if (value.length() > 70) {
            businessErrors.add("Họ quá dài (tối đa 35 ký tự)");
        }
        
        if (!VIETNAMESE_NAME_PATTERN.matcher(value).matches()) {
            businessErrors.add("Họ không hợp lệ");
        }
        
        if (businessErrors.isEmpty()) {
            return new ValueObjectValidationResult(new LastName(value), businessErrors);
        }
        
        return new ValueObjectValidationResult(null, businessErrors);
        
    }
    /**
     * @return 
     */
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(value);
    }
    
    private String getValue() {
        return normalize(value);
    }
    
    /**
     * @param value 
     * @return 
     */
    private String normalize(String value) {
        return value.trim().toUpperCase();
    }
}
