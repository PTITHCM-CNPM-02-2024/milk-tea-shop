package com.mts.backend.domain.product.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import lombok.Value;

import java.io.Serializable;
import java.util.Objects;
@Value
public class ProductId implements Serializable {
   int value;

    private ProductId(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Product id phải lớn hơn 0");
        }
        int MAX_VALUE = IdentifiableProvider.MEDIUMINT_UNSIGNED_MAX;
        if (value > MAX_VALUE) {
            throw new IllegalArgumentException("Product id phải nhỏ hơn " + MAX_VALUE);
        }
        this.value = value;
    }
    
    public static ProductId of (int value) {
        return new ProductId(value);
    }
    
    public static ProductId of(String value) {
        Objects.requireNonNull(value, "value is required");
        return new ProductId(Integer.parseInt(value));
    }
    
    /**
     * TODO: Đây là phương thức cần kiểm ra lại
     * FIX : smallint
     */
    public static ProductId create(){
        return new ProductId(IdentifiableProvider.generateTimeBasedUnsignedMediumInt());
    }
    
    public static final class ProductIdConverter implements jakarta.persistence.AttributeConverter<ProductId, Integer> {
        /**
         * @param attribute the entity attribute value to be converted
         * @return
         */
        @Override
        public Integer convertToDatabaseColumn(ProductId attribute) {
            return attribute.getValue();
        }

        /**
         * @param dbData the data from the database column to be
         *               converted
         * @return
         */
        @Override
        public ProductId convertToEntityAttribute(Integer dbData) {
            return new ProductId(dbData);
        }
    }
}
