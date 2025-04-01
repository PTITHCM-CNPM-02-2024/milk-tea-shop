package com.mts.backend.domain.product.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Value
@Builder
public class ProductName {
    
    String value;
    
    private static final int MAX_LENGTH = 100;
    
    private ProductName(String value) {
        
        Objects.requireNonNull(value, "Tên sản phẩm không được để trống");
        
        List<String> businessErrors = new ArrayList<>();

        if (value.trim().isEmpty()) {
            businessErrors.add("Tên không được để trống");
        }

        if (value.length() > MAX_LENGTH) {
            businessErrors.add("Tên không được quá " + MAX_LENGTH + " ký tự");
        }
        
        if (!businessErrors.isEmpty()) {
            throw new DomainBusinessLogicException(businessErrors);
        }
        this.value = normalize(value);
    }

    /**
     * Convert to uppercase and trim
     * @param value
     * @return
     */
    private static String normalize(String value) {
        return value.trim().toUpperCase(Locale.of("vi", "VN"));
    }
    
    
    public static final class ProductNameConverter implements jakarta.persistence.AttributeConverter<ProductName, String> {
        @Override
        public String convertToDatabaseColumn(ProductName attribute) {
            return attribute == null ? null : attribute.getValue();
        }

        @Override
        public ProductName convertToEntityAttribute(String dbData) {
            return dbData == null ? null : new ProductName(dbData);
        }
    }

    
}
