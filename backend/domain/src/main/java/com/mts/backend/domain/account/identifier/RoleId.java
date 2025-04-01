package com.mts.backend.domain.account.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;


@Value
public class RoleId implements Serializable { 
    Integer id;
    
    private RoleId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        if (id > IdentifiableProvider.TINYINT_UNSIGNED_MAX) {
            throw new IllegalArgumentException("Id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        }

        this.id = id;
    }
    
    public static RoleId of(int id) {
        return new RoleId(id);
    }
    
    public static RoleId of(String id) {
        Objects.requireNonNull(id, "Id cannot be null");
        return new RoleId(Integer.parseInt(id));
    }
    
    public static RoleId of(Integer id) {
        return new RoleId(id);
    }
    
    public static RoleId create() {
        return new RoleId(IdentifiableProvider.generateTimeBasedUnsignedTinyInt());
    }
    
    public int getValue() {
        return id;
    }
    
    public static final class RoleIdConverter implements AttributeConverter<RoleId, Integer> {
        @Override
        public Integer convertToDatabaseColumn(RoleId attribute) {
            return attribute.getValue();
        }
        
        @Override
        public RoleId convertToEntityAttribute(Integer dbData) {
            return new RoleId(dbData);
        }
    }
}
