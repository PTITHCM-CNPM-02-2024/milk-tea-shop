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
public class StoreName{ 
    String value;
    
    private static final int MAX_LENGTH = 100;
    private StoreName(String value) {

        Objects.requireNonNull(value, "Store name is required");

        List<String> errors = new ArrayList<>();

        if (value.length() > MAX_LENGTH){
            errors.add("Tên cửa hàng không được vượt quá " + MAX_LENGTH + " ký tự");
        }

        if (value.isBlank()){
            errors.add("Tên cửa hàng không được để trống");
        }
        
        if (!errors.isEmpty()){
            throw new DomainBusinessLogicException(errors);
        }
        this.value = normalize(value);
    }
    
    public final static class StoreNameConverter implements AttributeConverter<StoreName, String> {
        @Override
        public String convertToDatabaseColumn(StoreName attribute) {
            return Objects.isNull(attribute) ? null : attribute.getValue();
        }
    
        @Override
        public StoreName convertToEntityAttribute(String dbData) {
            return Objects.isNull(dbData) ? null : StoreName.builder().value(dbData).build();
        }
    }
    
    private static String normalize(String value){
        return value.trim().toUpperCase();
    }
}
