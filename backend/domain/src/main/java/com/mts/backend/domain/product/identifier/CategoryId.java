package com.mts.backend.domain.product.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;

import java.io.Serializable;
import java.util.Objects;

@Value
public class CategoryId implements Serializable {
Integer value;
    
    private CategoryId(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Category id phải lớn hơn 0");
        }
        if (value > IdentifiableProvider.SMALLINT_UNSIGNED_MAX){
            throw new IllegalArgumentException("Category id phải nhỏ hơn " + IdentifiableProvider.SMALLINT_UNSIGNED_MAX);
        }
        this.value = value;
    }
    
    public static CategoryId of (int value) {
        return new CategoryId(value);
    }
    
    public static CategoryId of(String value) {
        Objects.requireNonNull(value,"value is required");
        return new CategoryId(Integer.parseInt(value));
    }
    
    /**
     * TODO: Đây là phương thức cần kiểm ra lại
     */
    public static CategoryId create() {
        return new CategoryId(IdentifiableProvider.generateTimeBasedUnsignedSmallInt());
    }
    
    public static final class CategoryIdConverter implements AttributeConverter<CategoryId, Integer>{

        /**
         * @param attribute the entity attribute value to be converted 
         * @return
         */
        @Override
        public Integer convertToDatabaseColumn(CategoryId attribute) {
            return attribute.getValue();
        }

        /**
         * @param dbData the data from the database column to be 
         *               converted
         * @return
         */
        @Override
        public CategoryId convertToEntityAttribute(Integer dbData) {
            return new CategoryId(dbData);
        }
    }
}
