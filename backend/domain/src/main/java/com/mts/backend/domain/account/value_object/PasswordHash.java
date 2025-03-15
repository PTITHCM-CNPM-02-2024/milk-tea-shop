package com.mts.backend.domain.account.value_object;

import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.List;

public class PasswordHash extends AbstractValueObject {
    
    private final String value;
    
    private PasswordHash(String value) {
        this.value = value;
    }
    
    public static ValueObjectValidationResult create(String value) {
        if (value.isBlank()) {
            return new ValueObjectValidationResult(null, List.of("Mật khẩu không được để trống"));
        }
        return new ValueObjectValidationResult(new PasswordHash(value), List.of());
    }
    
    public static PasswordHash of(String value) {
        
        ValueObjectValidationResult result = create(value);
        if (result.getBusinessErrors().isEmpty()) {
            return new PasswordHash(value);
        }
        throw new DomainException("Mật khẩu không hợp lệ: " + result.getBusinessErrors());
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
