package com.mts.backend.domain.store.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.AttributeConverter;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Value
@Builder
public class TableNumber {
    String value;
    private static final int MAX_LENGTH = 10;
    private TableNumber(String value){
        Objects.requireNonNull(value, "Table number is required");
        
        List<String> errors = new ArrayList<>();
        
        if (value.length() > MAX_LENGTH){
            errors.add("Mã bàn không được vượt quá " + MAX_LENGTH + " ký tự");
        }
        
        if (value.isBlank()){
            errors.add("Mã bàn không được để trống");
        }
        
        if (!errors.isEmpty()){
            throw new DomainBusinessLogicException(errors);
        }
        
        
        this.value = normalize(value);
    }
    
    private static String normalize(String value){
        return value.trim().toUpperCase();
    }
    
    public String getValue(){
        return value;
    }
    
    public static final class TableNumberConverter implements AttributeConverter<TableNumber, String> {
        @Override
        public String convertToDatabaseColumn(TableNumber attribute) {
            return Objects.isNull(attribute) ? null : attribute.getValue();
        }
        
        @Override
        public TableNumber convertToEntityAttribute(String dbData) {
            return Objects.isNull(dbData) ? null : TableNumber.builder().value(dbData).build();
        }
    }
    
}
