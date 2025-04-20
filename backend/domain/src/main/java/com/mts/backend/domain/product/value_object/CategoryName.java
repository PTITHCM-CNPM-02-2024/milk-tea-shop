package com.mts.backend.domain.product.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.AttributeConverter;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
@Value(staticConstructor = "of")
public class CategoryName {
    
    private static final int MAX_LENGTH = 100;
    @jakarta.validation.constraints.Size(max = MAX_LENGTH, message = "Tên không được vượt quá " + MAX_LENGTH + " ký tự")
    @jakarta.validation.constraints.NotBlank(message = "Tên không được để trống")
    String value;

    public CategoryName(@jakarta.validation.constraints.NotBlank(message = "Tên không được để trống") @jakarta.validation.constraints.Size(max = MAX_LENGTH, message = "Tên không được vượt quá " + MAX_LENGTH + " ký tự") String value) {
        this.value = normalize(value);
    }
    
    private static String normalize(String value) {
        return value.toUpperCase().trim();
    }
    


}
