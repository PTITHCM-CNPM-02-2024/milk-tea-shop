package com.mts.backend.domain.product.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.AttributeConverter;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Value(staticConstructor = "of")
public class ProductSizeName {
    private static final int MAX_LENGTH = 5;
    @jakarta.validation.constraints.NotBlank(message = "Tên không được để trống")
    @jakarta.validation.constraints.Size(max = 5, message = "Tên không được vượt quá 5 ký tự")
    String value;

    public ProductSizeName(
            @jakarta.validation.constraints.NotBlank(message = "Tên không được để trống")
            @jakarta.validation.constraints.Size(max = 5, message = "Tên không được vượt quá 5 ký tự") String value) {
        this.value = normalize(value);
    }

    private String normalize(String name) {
        return name.toUpperCase().trim();
    }
    

}
