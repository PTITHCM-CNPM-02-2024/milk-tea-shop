package com.mts.backend.domain.store.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.AttributeConverter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Value(staticConstructor = "of")
public class Address {
    @Size(max = MAX_LENGTH, message = "Địa chỉ không được vượt quá 255 ký tự")
    @NotBlank(message = "Địa chỉ không được để trống")       
    String value;
    private static final int MAX_LENGTH = 255;
}
