package com.mts.backend.domain.account.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.shared.domain.Identifiable;

import java.util.Objects;

public class AccountId implements Identifiable {
    
    private final CouponId id;
    
    private AccountId(CouponId id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        if (id > IdentifiableProvider.INT_UNSIGNED_MAX) {
            throw new IllegalArgumentException("Id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        }
        
        this.id = id;
    }
    
    public static AccountId of(CouponId id) {
        return new AccountId(id);
    }
    
    public static AccountId of(String id) {
        Objects.requireNonNull(id, "Id cannot be null");
        return new AccountId(Long.parseLong(id));
    }
    
    public CouponId getValue() {
        return id;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        AccountId accountId = (AccountId) o;
        
        return id == accountId.id;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
    
    @Override
    public String toString() {
        return String.valueOf(id);
    }
    
    public static AccountId create() {
        return new AccountId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
}
