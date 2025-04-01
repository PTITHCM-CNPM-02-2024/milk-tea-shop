package com.mts.backend.domain.promotion.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;

import java.io.Serializable;
@Value
public class CouponId implements Serializable {
    Long value;
    
    private  CouponId(long value){
        if (value <= 0){
            throw new IllegalArgumentException("CouponId must be greater than 0");
        }

        if (value > IdentifiableProvider.INT_UNSIGNED_MAX){
            throw new IllegalArgumentException("CouponId must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        }
        this.value = value;
    }
    
    public static CouponId of(long value){
        return new CouponId(value);
    }
    
    public static CouponId of(String value){
        return of(Long.parseLong(value));
    }
    
    public Long getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
    public static CouponId create(){
        return new CouponId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
    
    public static final class CouponIdConverter implements AttributeConverter<CouponId, Long> {
        @Override
        public Long convertToDatabaseColumn(CouponId attribute) {
            return attribute.getValue();
        }
        
        @Override
        public CouponId convertToEntityAttribute(Long dbData) {
            return CouponId.of(dbData);
        }
    }
}
