package com.mts.backend.domain.product.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Value
@Builder
public class UnitSymbol{
    String value;
    
    private static final int MAX_LENGTH = 5;
    
    private UnitSymbol(String value) {
        Objects.requireNonNull(value, "Ký hiệu đơn vị đo không được để trống");
        List<String> businessErrors = new ArrayList<>();

        if (value.trim().isEmpty()) {
            businessErrors.add("Ký hiệu đơn vị đo không được để trống");
        }

        if (value.length() > MAX_LENGTH) {
            businessErrors.add("Ký hiệu đơn vị đo không được quá " + MAX_LENGTH + " ký tự");
        }
        
        if (!businessErrors.isEmpty()) {
            throw new DomainBusinessLogicException(businessErrors);
        }
        this.value = value;
    }
    
    public static final class UnitSymbolConverter implements jakarta.persistence.AttributeConverter<UnitSymbol, String> {
        @Override
        public String convertToDatabaseColumn(UnitSymbol attribute) {
            return attribute == null ? null : attribute.getValue();
        }

        @Override
        public UnitSymbol convertToEntityAttribute(String dbData) {
            return dbData == null ? null : new UnitSymbol(dbData);
        }
    }
    
}
