package com.mts.backend.domain.account.value_object;

import jakarta.persistence.AttributeConverter;
import lombok.Value;

import java.util.Objects;
import java.util.regex.Pattern;

@Value(staticConstructor = "of")
public class Username {

    private final static Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,50}$");
    @jakarta.validation.constraints.Size(max = 20, min = 3, message = "Tên đăng nhập ${validatedValue} không hợp lệ, " +
                                                                      "phải từ ${min} đến ${max} ký tự")
    @jakarta.validation.constraints.NotBlank(message = "Tên đăng nhập không được để trống")
    @jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9_-]{3,50}$", message = "Tên đăng nhập không hợp lệ")
    String value;

    public Username(@jakarta.validation.constraints.Size(max = 50, min = 3, message = "Tên đăng nhập " +
                                                                                      "${validatedValue} không hợp lệ, " +
                                                                                      "phải từ ${min} đến ${max} ký tự") @jakarta.validation.constraints.NotBlank(message = "Tên đăng nhập không được để trống") String value) {
        this.value = value;
    }
    
}
