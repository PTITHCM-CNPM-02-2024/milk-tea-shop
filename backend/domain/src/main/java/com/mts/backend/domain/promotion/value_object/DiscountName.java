package com.mts.backend.domain.promotion.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.AttributeConverter;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Value(staticConstructor = "of")
public class DiscountName {
    private final static int MAX_LENGTH = 500;
    @jakarta.validation.constraints.NotBlank(message = "Tên khuyến mãi không được để trống")
    @Size(max = 500, message = "Tên khuyến mãi không được quá 500 ký tự")
    String value;

    public DiscountName(@jakarta.validation.constraints.NotBlank(message = "Tên khuyến mãi không được để trống") 
                        @Size(max = 500, message = "Tên khuyến mãi không được quá 500 ký tự") String value) {
        this.value = value;
    }
}
