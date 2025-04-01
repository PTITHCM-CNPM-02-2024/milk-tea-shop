package com.mts.backend.domain.store.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.exception.DomainException;
import jakarta.persistence.AttributeConverter;
import lombok.Value;

import java.io.Serializable;

@Value
public class StoreId implements Serializable {
     int value;
    
    private StoreId(int value) {
        if (value <= 0) {
            throw new DomainException("Store id không hợp lệ");
        }
        if (value > IdentifiableProvider.TINYINT_UNSIGNED_MAX) {
            throw new DomainException("Store id không hợp lệ");
        }
        this.value = value;
    }
    
    public static StoreId of(int storeId) {
        return new StoreId(storeId);
    }
    
    public static StoreId of (String storeId) {
        return new StoreId(Integer.parseInt(storeId));
    }
    
    public static final  class  StoreIdConverter implements AttributeConverter<StoreId, Integer> {
        @Override
        public Integer convertToDatabaseColumn(StoreId attribute) {
            return attribute.getValue();
        }
        
        @Override
        public StoreId convertToEntityAttribute(Integer dbData) {
            return StoreId.of(dbData);
        }
    }
    
    public static StoreId create(){
        return new StoreId(IdentifiableProvider.generateTimeBasedUnsignedTinyInt());
    }
    
}
