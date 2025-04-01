package com.mts.backend.domain.product.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.AttributeConverter;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
@Value
@Builder
public class UnitName {
    
    String value;
    
    private static final int MAX_LENGTH = 10;
    
    private UnitName(String value) {

        List<String> businessErrors = new ArrayList<>();
        if (value == null || value.trim().isEmpty()) {
            businessErrors.add("Tên đơn vị đo không được để trống");
        }

        if (value != null && value.length() > MAX_LENGTH) {
            businessErrors.add("Tên đơn vị đo không được quá " + MAX_LENGTH + " ký tự");
        }
        
        if (!businessErrors.isEmpty()){
            throw new DomainBusinessLogicException(businessErrors);
        }
        this.value = value;
    }
    
    public static final class UnitNameConverter implements AttributeConverter<UnitName, String> {
        @Override
        public String convertToDatabaseColumn(UnitName attribute) {
            return attribute == null ? null : attribute.getValue();
        }

        @Override
        public UnitName convertToEntityAttribute(String dbData) {
            return dbData == null ? null : new UnitName(dbData);
        }
    }
    
}
