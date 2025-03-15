package com.mts.backend.domain.account.value_object;

import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class RoleName extends AbstractValueObject {
    
    private final String value;
    
    private final static Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    
    private RoleName(String value) {
        Objects.requireNonNull(value, "Role name is required");
        this.value = value;
    }
    
    public static ValueObjectValidationResult create(String value) {
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
        
        if (businessErrors.isEmpty()) {
            return new ValueObjectValidationResult(new RoleName(value), businessErrors);
        }
        
        return new ValueObjectValidationResult(new RoleName(value), businessErrors);
    }
    
    public static RoleName of(String value) {
        ValueObjectValidationResult result = create(value);
        if (result.getBusinessErrors().isEmpty()) {
            return new RoleName(value);
        }
        throw new IllegalArgumentException("Tên vai trò không hợp lệ: " + result.getBusinessErrors());
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * @return 
     */
    @Override
    protected Iterable<Object> getEqualityComponents() {
        return List.of(value);
    }
}
