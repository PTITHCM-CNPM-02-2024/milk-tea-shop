package com.mts.backend.domain.product.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;

import java.io.Serializable;

@Value
public class ProductPriceId implements Serializable {
    long value;
    
    private ProductPriceId(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Product price id phải lớn hơn 0");
        }
        if (value > IdentifiableProvider.INT_UNSIGNED_MAX){
            throw new IllegalArgumentException("Product price id phải nhỏ hơn " + IdentifiableProvider.INT_UNSIGNED_MAX);
        }
        this.value = value;
    }
    
    public static ProductPriceId of (long value) {
        return new ProductPriceId(value);
    }
    
    public static ProductPriceId of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Product price id cannot be null");
        }
        return new ProductPriceId(Long.parseLong(value));
    }
    
    /**
     * TODO: Cần kiểm tra xem có cần thiết không
     */
    public static ProductPriceId create() {
        return new ProductPriceId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
    
    public static final class ProductPriceIdConverter implements AttributeConverter<ProductPriceId, Long> {
        @Override
        public Long convertToDatabaseColumn(ProductPriceId attribute) {
            return attribute.getValue();
        }
        
        @Override
        public ProductPriceId convertToEntityAttribute(Long dbData) {
            return ProductPriceId.of(dbData);
        }
    }
}
