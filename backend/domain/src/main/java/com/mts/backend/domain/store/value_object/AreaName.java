package com.mts.backend.domain.store.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Value
@Builder
public class AreaName {
    String value;
    private static final int MAX_LENGTH = 3;
    
    private AreaName(String value) {
        Objects.requireNonNull(value);
        
        List<String> errors = new ArrayList<>();
        
        if (value.length() > MAX_LENGTH) {
            errors.add("Tên khu vực phải có đúng" + MAX_LENGTH + "ký tự");
        }
        
        if (value.isBlank()){
            errors.add("Tên bàn không được để trống");
        }
        
        if (!errors.isEmpty()){
            throw new DomainBusinessLogicException(errors);
        }
        
        this.value = normalize(value);
    }
    
    private static String normalize(String value){
        return value.trim().toUpperCase();
    }
        
    @Converter(autoApply = true)
    public static class AreaNameConverter implements AttributeConverter<AreaName, String> {
        @Override
        public String convertToDatabaseColumn(AreaName attribute) {
            return Objects.isNull(attribute) ? null : attribute.getValue();
        }
    
        @Override
        public AreaName convertToEntityAttribute(String dbData) {
            return Objects.isNull(dbData) ? null : AreaName.builder().value(dbData).build();
        }
    }
}
