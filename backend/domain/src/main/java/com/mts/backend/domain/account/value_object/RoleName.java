package com.mts.backend.domain.account.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.AttributeConverter;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Value(staticConstructor = "of")
public class RoleName {
    
    private final static Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    @jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$",
            message = "Tên vai trò không hợp lệ")
    @jakarta.validation.constraints.NotBlank(message = "Tên vai trò không được để trống")
    String value;

    public RoleName(@jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$",
            message = "Tên vai trò không hợp lệ") @jakarta.validation.constraints.NotBlank(message = "Tên vai trò không được để trống") String value) {
        this.value = normalize(value);
    }

    private static String normalize(String value) {
        return value.trim().replaceAll("\\s+", " ").toUpperCase();
    }
    
}
