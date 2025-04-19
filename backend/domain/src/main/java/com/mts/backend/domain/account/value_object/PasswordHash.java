package com.mts.backend.domain.account.value_object;

import com.mts.backend.shared.exception.DomainException;
import jakarta.persistence.AttributeConverter;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

@Value(staticConstructor = "of")
public class PasswordHash {
    @jakarta.validation.constraints.Size(max = 255, message = "Mật khẩu không được vượt quá 255 ký tự")
    @jakarta.validation.constraints.NotBlank(message = "Mật khẩu không được để trống")
    String value;

    public PasswordHash(
            @jakarta.validation.constraints.Size(max = 255, message = "Mật khẩu không được vượt quá 255 ký tự")
            @jakarta.validation.constraints.NotBlank(message = "Mật khẩu không được để trống") String value) {
        this.value = value;
    }
    
}
