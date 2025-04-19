package com.mts.backend.domain.store.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.AttributeConverter;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Value(staticConstructor = "of")
public class TableNumber {
    @Size(max = MAX_LENGTH, message = "Mã bàn không được vượt quá 10 ký tự")
    @jakarta.validation.constraints.NotBlank(message = "Mã bàn không được để trống")
    String value;
    private static final int MAX_LENGTH = 10;
    public TableNumber(@Size(max = MAX_LENGTH, message = "Mã bàn không được vượt quá 10 ký tự") @jakarta.validation.constraints.NotBlank(message = "Mã bàn không được để trống") String value) {
        this.value = normalize(value);
    }
    
    private static String normalize(String value){
        return value.trim().toUpperCase();
    }
    
}
