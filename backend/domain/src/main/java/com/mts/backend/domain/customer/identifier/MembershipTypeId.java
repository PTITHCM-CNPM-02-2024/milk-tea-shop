package com.mts.backend.domain.customer.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;

import java.io.Serializable;
@Value
public class MembershipTypeId implements Serializable {
int value;
    
    private MembershipTypeId(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        if (value > IdentifiableProvider.TINYINT_UNSIGNED_MAX){
            throw new IllegalArgumentException("Id must be less than " + IdentifiableProvider.TINYINT_UNSIGNED_MAX);
        }
        this.value = value;
    }
    
    public static MembershipTypeId of(int id) {
        return new MembershipTypeId(id);
    }
    
    public static MembershipTypeId of(String id) {
        return new MembershipTypeId(Integer.parseInt(id));
    }
    
    public static MembershipTypeId create() {
        return new MembershipTypeId(IdentifiableProvider.generateTimeBasedUnsignedTinyInt());
    }
    
    public static final class MembershipTypeIdConverter implements AttributeConverter<MembershipTypeId, Integer> {
        @Override
        public Integer convertToDatabaseColumn(MembershipTypeId attribute) {
            return attribute.getValue();
        }
        
        @Override
        public MembershipTypeId convertToEntityAttribute(Integer dbData) {
            return MembershipTypeId.of(dbData);
        }
    }
}
