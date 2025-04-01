package com.mts.backend.domain.store.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;

import java.io.Serializable;

@Value
public class ServiceTableId implements Serializable {
    int value;
    
    private ServiceTableId(int id){
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid serviceTableId");
        }
        if (id > IdentifiableProvider.SMALLINT_UNSIGNED_MAX) {
            throw new IllegalArgumentException("Invalid serviceTableId");
        }
        
        this.value = id;
    }
    
    public static ServiceTableId of (int id){
        return new ServiceTableId(id);
    }
    
    public static ServiceTableId of (String id){
        return new ServiceTableId(Integer.parseInt(id));
    }
    
    public static ServiceTableId create(){
        return new ServiceTableId(IdentifiableProvider.generateTimeBasedUnsignedSmallInt());
    }
    
    
    public static final class ServiceTableIdConverter implements AttributeConverter<ServiceTableId, Integer> {
        @Override
        public Integer convertToDatabaseColumn(ServiceTableId attribute) {
            return attribute.getValue();
        }
        
        @Override
        public ServiceTableId convertToEntityAttribute(Integer dbData) {
            return ServiceTableId.of(dbData);
        }
    }
}
