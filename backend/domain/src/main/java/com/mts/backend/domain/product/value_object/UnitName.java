package com.mts.backend.domain.product.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.AttributeConverter;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value(staticConstructor = "of")
public class UnitName {
    private static final int MAX_LENGTH = 30;
    @jakarta.validation.constraints.NotBlank(message = "Tên đơn vị đo không được để trống")
    @jakarta.validation.constraints.Size(max = MAX_LENGTH, message = "Tên đơn vị đo không được vượt quá " + MAX_LENGTH + " ký tự")
    String value;

    public UnitName(
            @jakarta.validation.constraints.NotBlank(message = "Tên đơn vị đo không được để trống")
            @jakarta.validation.constraints.Size(max = MAX_LENGTH, message = "Tên đơn vị đo không được vượt quá " + MAX_LENGTH + " ký tự") String value) {
        this.value = value;
    }
}
