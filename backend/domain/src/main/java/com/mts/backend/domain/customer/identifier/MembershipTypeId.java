package com.mts.backend.domain.customer.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;

public class MembershipTypeId implements Identifiable {
    private final int id;
    
    private MembershipTypeId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        if (id > IdentifiableProvider.TINYINT_UNSIGNED_MAX){
            throw new IllegalArgumentException("Id must be less than " + IdentifiableProvider.TINYINT_UNSIGNED_MAX);
        }
        this.id = id;
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
    
    public int getValue() {
        return id;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        MembershipTypeId membershipTypeId = (MembershipTypeId) o;
        
        return id == membershipTypeId.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    @Override
    public String toString() {
        return String.valueOf(id);
    }
    
}
