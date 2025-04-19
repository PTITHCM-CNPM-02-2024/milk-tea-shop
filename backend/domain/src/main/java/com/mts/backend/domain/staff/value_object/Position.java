package com.mts.backend.domain.staff.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Value(staticConstructor = "of")
public class Position{
    @jakarta.validation.constraints.Size(max = MAX_LENGTH, message = "Tên chức vụ không được vượt quá 50 ký tự")
    @jakarta.validation.constraints.NotBlank(message = "Tên chức vụ không được để trống")
    String value;
    private final static short MAX_LENGTH = 50;
    public Position(@jakarta.validation.constraints.Size(max = MAX_LENGTH, message = "Tên chức vụ không được vượt quá 50 ký tự") @jakarta.validation.constraints.NotBlank(message = "Tên chức vụ không được để trống") String value) {
        this.value = normalize(value);
    }
    private static String normalize(String position) {
        return position.trim().toUpperCase();
    }
}
