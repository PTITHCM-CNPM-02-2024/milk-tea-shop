package com.mts.backend.domain.common.value_object;

import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class FirstName extends AbstractValueObject {

    private final String value;
    
    private final static Pattern VIETNAMESE_NAME_PATTERN = Pattern.compile("^[A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ][a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]*(?:[ ][A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ][a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]*)*$");

    private FirstName(String value) {
        this.value = normalize(value);
    }


    public String getValue() {
        return normalize(value);
    }


    public static ValueObjectValidationResult create(String value) {
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
        
        if (businessErrors.isEmpty()) {
            return new ValueObjectValidationResult(new FirstName(value), businessErrors);
        }

        return new ValueObjectValidationResult(null, businessErrors);
    }

    public static FirstName of(String value) {
        ValueObjectValidationResult result = create(value);
        if (result.getBusinessErrors().isEmpty()) {
            return new FirstName(value);
        }
        throw new DomainException("Tên không hợp lệ: " + result.getBusinessErrors());
    }


    @Override
    public String toString() {
        return value;
    }
    
    private String normalize(String value) {
        return value.trim().toUpperCase();
    }

    /**
     * @return
     */
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(value);
    }
}
