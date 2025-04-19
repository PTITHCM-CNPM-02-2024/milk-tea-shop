package com.mts.backend.domain.store.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Value(staticConstructor = "of")
public class AreaName {
    @jakarta.validation.constraints.Size(max = MAX_LENGTH, min = 3, message = "Tên khu vực phải có đúng 3 ký tự")
    @jakarta.validation.constraints.NotBlank(message = "Tên khu vực không được để trống")
    String value;
    private static final int MAX_LENGTH = 3;
    
    public AreaName(@jakarta.validation.constraints.Size(max = MAX_LENGTH, min = 3, message = "Tên khu vực phải có đúng 3 ký tự") @jakarta.validation.constraints.NotBlank(message = "Tên khu vực không được để trống") String value) {
        this.value = normalize(value);
    }
    
    private static String normalize(String value){
        return value.trim().toUpperCase();
    }
}
