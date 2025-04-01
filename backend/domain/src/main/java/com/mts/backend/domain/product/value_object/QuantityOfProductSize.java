package com.mts.backend.domain.product.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
@Value
@Builder
public class QuantityOfProductSize {
    int value;
    
    private QuantityOfProductSize(int value) {
        List<String> businessErrors = new ArrayList<>();
        if (value <= 0) {
            businessErrors.add("Số lượng phải lớn hơn 0");
        }
        if (!businessErrors.isEmpty()) {
            throw new DomainBusinessLogicException(businessErrors);
        }
        this.value = value;
    }
    public static final class QuantityOfProductSizeConverter implements jakarta.persistence.AttributeConverter<QuantityOfProductSize, Integer> {
        @Override
        public Integer convertToDatabaseColumn(QuantityOfProductSize attribute) {
            return attribute == null ? null : attribute.getValue();
        }

        @Override
        public QuantityOfProductSize convertToEntityAttribute(Integer dbData) {
            return dbData == null ? null : new QuantityOfProductSize(dbData);
        }
    }
    
    
}
