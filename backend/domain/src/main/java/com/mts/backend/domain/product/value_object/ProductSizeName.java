package com.mts.backend.domain.product.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.AttributeConverter;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Value
@Builder
public class ProductSizeName {
   String value;
    
    private static final int MAX_LENGTH = 5;
    private ProductSizeName(String value) {
        Objects.requireNonNull(value, "Tên kích thước không được để trống");

        List<String> businessErrors = new ArrayList<>();

        if (value.isBlank()) {
            businessErrors.add("Tên không được để trống");
        }

        if (value.length() > MAX_LENGTH) {
            businessErrors.add("Tên không được quá " + MAX_LENGTH + " ký tự");
        }
        
        if (!businessErrors.isEmpty()) {
            throw new DomainBusinessLogicException(businessErrors);
        }
        this.value = value;
    }
    
    private String normalize(String name) {
        return name.toUpperCase().trim();
    }
    
    public String getValue() {
        return normalize(value);
    }
    
    public static final class ProductSizeNameConverter implements AttributeConverter<ProductSizeName, String> {
        @Override
        public String convertToDatabaseColumn(ProductSizeName attribute) {
            return attribute == null ? null : attribute.getValue();
        }

        @Override
        public ProductSizeName convertToEntityAttribute(String dbData) {
            return dbData == null ? null : new ProductSizeName(dbData);
        }
    }
    
}
