package com.mts.backend.domain.customer.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;
import com.mts.backend.shared.exception.DomainException;
import lombok.Value;

import java.io.Serializable;

@Value

public class RewardPointLogId implements Identifiable, Serializable {
    
   long id;
    
    private RewardPointLogId(long id) {
        
        if (id <= 0) {
            throw new DomainException("RewardPointLog id must be greater than 0");
        }
        
        if (id > IdentifiableProvider.INT_UNSIGNED_MAX)
            throw new DomainException("RewardPointLog id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        this.id = id;
    }
    
    public static RewardPointLogId of(long id) {
        return new RewardPointLogId(id);
    }
    
    public static RewardPointLogId of(String id) {
        return new RewardPointLogId(Long.parseLong(id));
    }
    
    public static RewardPointLogId create(){
        return new RewardPointLogId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
    
    public static final class RewardPointLogIdConverter implements jakarta.persistence.AttributeConverter<RewardPointLogId, Long> {
        @Override
        public Long convertToDatabaseColumn(RewardPointLogId attribute) {
            return attribute.getId();
        }
        
        @Override
        public RewardPointLogId convertToEntityAttribute(Long dbData) {
            return RewardPointLogId.of(dbData);
        }
    }
    
}
