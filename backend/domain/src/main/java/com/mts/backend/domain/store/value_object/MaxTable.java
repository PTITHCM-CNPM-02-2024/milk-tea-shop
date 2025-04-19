package com.mts.backend.domain.store.value_object;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.Builder;
import lombok.Value;
import org.hibernate.validator.constraints.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Value(staticConstructor = "of")
public class MaxTable{
    @Range(min = 0, max = 100, message = "Số bàn tối đa không được vượt quá 100")
    int value;
    private final static int MAX_TABLE = 100;
    
    public MaxTable(@Range(min = 0, max = MAX_TABLE, message = "Số bàn tối đa không được vượt quá 100") int value) {
        this.value = value;
    }
    
    
}
