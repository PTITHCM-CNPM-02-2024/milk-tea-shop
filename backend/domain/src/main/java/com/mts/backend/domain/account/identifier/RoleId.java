package com.mts.backend.domain.account.identifier;

import com.mts.backend.domain.account.Role;
import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;

import java.util.Objects;


public class RoleId implements Identifiable {
    private final int id;
    
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
    
    public static RoleId create() {
        return new RoleId(IdentifiableProvider.generateTimeBasedUnsignedTinyInt());
    }
    
    public int getValue() {
        return id;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        RoleId roleId = (RoleId) o;
        
        return id == roleId.id;
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
