package com.mts.backend.domain.promotion.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;

import java.io.Serializable;

@Value
public class DiscountId implements Serializable{
    long value;
    
    private  DiscountId(long value){
        if (value <= 0){
            throw new IllegalArgumentException("DiscountId must be greater than 0");
        }

        if (value > IdentifiableProvider.INT_UNSIGNED_MAX){
            throw new IllegalArgumentException("DiscountId must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        }

        this.value = value;
    }
    
    public static DiscountId of(long value){

        return new DiscountId(value);
    }
    
    public static DiscountId of(String value){
        return of(Long.parseLong(value));
    }
    
    public static DiscountId create(){
        return new DiscountId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
    
    public static final class DiscountIdConverter implements AttributeConverter<DiscountId, Long> {
        @Override
        public Long convertToDatabaseColumn(DiscountId attribute) {
            return attribute.getValue();
        }
        
        @Override
        public DiscountId convertToEntityAttribute(Long dbData) {
            return DiscountId.of(dbData);
        }
    }
}
