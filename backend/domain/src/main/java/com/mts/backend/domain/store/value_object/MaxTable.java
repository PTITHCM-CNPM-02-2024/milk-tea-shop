package com.mts.backend.domain.store.value_object;

import com.mts.backend.shared.value_object.AbstractValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Value
@Builder
public class MaxTable{
    int value;
    private final static int MAX_TABLE = 100;
    
    private MaxTable(int value) {
        
        List<String> errors = new ArrayList<>();
        
        if (value > MAX_TABLE) {
            errors.add("Số bàn tối đa không được vượt quá " + MAX_TABLE);
        }
        
        if (value < 0){
            errors.add("Số bàn tối đa phải lớn hơn hoặc bằng 0");
        }
        
        this.value = value;
    }
    
    @Converter(autoApply = true)
    public final static class MaxTableConverter implements AttributeConverter<MaxTable, Integer> {
        
        @Override
        public Integer convertToDatabaseColumn(MaxTable attribute) {
            return Objects.isNull(attribute) ? null : attribute.getValue();
        }
        
        @Override
        public MaxTable convertToEntityAttribute(Integer dbData) {
            return Objects.isNull(dbData) ? null : MaxTable.builder().value(dbData).build();
        }
    }
    
}
