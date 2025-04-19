package com.mts.backend.domain.common.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.AttributeConverter;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Value(staticConstructor = "of")
public class PhoneNumber {

    @jakarta.validation.constraints.Pattern(regexp = "(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)", message = "Số điện thoại không hợp lệ")
    @NotBlank(message = "Số điện thoại không được để trống")
    String value;

    public PhoneNumber (@jakarta.validation.constraints.Pattern(regexp = "(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)", message = "Số điện thoại không hợp lệ") @NotBlank(message = "Số điện thoại không được để trống") String value) {
        this.value = normalize(value);
    }

    private static String normalize(String value) {
        return value.replaceAll("[^0-9]", "");
    }
    
}
