package com.mts.backend.domain.product.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.AttributeConverter;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
@Value
@Builder
public class CategoryName {
    
    private static final int MAX_LENGTH = 100;
    
    String value;

    public CategoryName(String value) {
        List<String> businessErrors = new ArrayList<>();

        if (value == null || value.trim().isEmpty()) {
            businessErrors.add("Tên không được để trống");
        }

        if (value != null && value.length() > MAX_LENGTH) {
            businessErrors.add("Tên không được quá " + MAX_LENGTH + " ký tự");
        }
        
        if (!businessErrors.isEmpty()){
            throw new DomainBusinessLogicException(businessErrors);
        }
        this.value = normalize(value);
    }
    
    private static String normalize(String value) {
        return value.toUpperCase().trim();
    }
    
    public String getValue() {
        return normalize(value);
    }

    public static final class CategoryNameConverter implements AttributeConverter<CategoryName, String>{
        @Override
        public String convertToDatabaseColumn(CategoryName attribute) {
            return attribute == null ? null : attribute.getValue();
        }

        @Override
        public CategoryName convertToEntityAttribute(String dbData) {
            return dbData == null ? null : new CategoryName(dbData);
        }
    }
}
