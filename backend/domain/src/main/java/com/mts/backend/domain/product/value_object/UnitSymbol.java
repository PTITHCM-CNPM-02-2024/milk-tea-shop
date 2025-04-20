package com.mts.backend.domain.product.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Value(staticConstructor = "of")
public class UnitSymbol{
    @jakarta.validation.constraints.NotBlank(message = "Ký hiệu đơn vị đo không được để trống")
    @jakarta.validation.constraints.Size(max = 5, message = "Ký hiệu đơn vị đo không được vượt quá 5 ký tự")
    String value;
    
    private static final int MAX_LENGTH = 5;
    
    public UnitSymbol(
            @jakarta.validation.constraints.NotBlank(message = "Ký hiệu đơn vị đo không được để trống")
            @jakarta.validation.constraints.Size(max = MAX_LENGTH, message = "Ký hiệu đơn vị đo không được vượt quá " + MAX_LENGTH + " ký tự") String value) {
        this.value = value;
    }
    
}
