package com.mts.backend.domain.account.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import lombok.Value;

import java.io.Serializable;
import java.util.Objects;
@Value
public class AccountId implements Serializable {
    Long value;
    
    private AccountId(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        if (value > IdentifiableProvider.INT_UNSIGNED_MAX) {
            throw new IllegalArgumentException("Id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        }
        
        this.value = value;
    }
    
    public static AccountId of(long id) {
        return new AccountId(id);
    }
    
    public static AccountId of(Long id) {
        return new AccountId(id);
    }
    
    public static AccountId of(String id) {
        Objects.requireNonNull(id, "Id cannot be null");
        return new AccountId(Long.parseLong(id));
    }
    public static AccountId create() {
        return new AccountId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
    
    
    public static final class AccountIdConverter implements jakarta.persistence.AttributeConverter<AccountId, Long> {
        @Override
        public Long convertToDatabaseColumn(AccountId attribute) {
            return attribute.getValue();
        }
        
        @Override
        public AccountId convertToEntityAttribute(Long dbData) {
            return AccountId.of(dbData);
        }
    }
}
