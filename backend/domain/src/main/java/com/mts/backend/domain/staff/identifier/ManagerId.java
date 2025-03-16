package com.mts.backend.domain.staff.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;

public class ManagerId implements Identifiable {
    private final long value;
    
    private ManagerId(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        if (value > IdentifiableProvider.INT_UNSIGNED_MAX) {
            throw new IllegalArgumentException("Id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        }
        
        this.value = value;
    }
    
    public static ManagerId of(long value) {
        return new ManagerId(value);
    }
    
    public static ManagerId of(String value) {
        return new ManagerId(Long.parseLong(value));
    }
    
    public long getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ManagerId managerId = (ManagerId) o;
        
        return value == managerId.value;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
    public static ManagerId create() {
        return new ManagerId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
    
}
