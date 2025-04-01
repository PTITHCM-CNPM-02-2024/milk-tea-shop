package com.mts.backend.domain.product.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import lombok.Value;

import java.io.Serializable;

@Value
public class ProductSizeId implements Serializable {
    Integer value;

    private ProductSizeId(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Size id phải lớn hơn 0");
        }
        if (value > IdentifiableProvider.SMALLINT_UNSIGNED_MAX){
            throw new IllegalArgumentException("Size id phải nhỏ hơn " + IdentifiableProvider.SMALLINT_UNSIGNED_MAX);
        }
        this.value = value;
    }


    public static ProductSizeId of(int value) {
        return new ProductSizeId(value);
    }

    public static ProductSizeId of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Size id cannot be null");
        }
        return new ProductSizeId(Integer.parseInt(value));
    }
    public static ProductSizeId create() {
        return new ProductSizeId(IdentifiableProvider.generateTimeBasedUnsignedSmallInt());
    }
    
    public static final class ProductSizeIdConverter implements jakarta.persistence.AttributeConverter<ProductSizeId, Integer> {
        @Override
        public Integer convertToDatabaseColumn(ProductSizeId attribute) {
            return attribute.getValue();
        }

        @Override
        public ProductSizeId convertToEntityAttribute(Integer dbData) {
            return new ProductSizeId(dbData);
        }
    }
}
