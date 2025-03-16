package com.mts.backend.domain.staff.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;

public class EmployeeId implements Identifiable {
    private final long value;
    
    private EmployeeId(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        if (value > IdentifiableProvider.INT_UNSIGNED_MAX) {
            throw new IllegalArgumentException("Id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        }
        
        this.value = value;
    }
    
    public static EmployeeId of(long value) {
        return new EmployeeId(value);
    }
    
    public static EmployeeId of(String value) {
        return new EmployeeId(Long.parseLong(value));
    }
    
    public long getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        EmployeeId employeeId = (EmployeeId) o;
        
        return value == employeeId.value;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
    public static EmployeeId create() {
        return new EmployeeId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
}
