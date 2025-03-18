package com.mts.backend.domain.store.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;


public class AreaId implements Identifiable {
    private final int areaId;
    
    private AreaId(int areaId) {
        if (areaId <= 0) {
            throw new IllegalArgumentException("Invalid areaId");
        }
        if (areaId > IdentifiableProvider.SMALLINT_UNSIGNED_MAX) {
            throw new IllegalArgumentException("Invalid areaId");
        }
        this.areaId = areaId;
    }
    
    public static AreaId of (int areaId) {
        return new AreaId(areaId);
    }
    
    public static AreaId of (String areaId) {
        return new AreaId(Integer.parseInt(areaId));
    }
    
    public int getValue(){
        return areaId;
    }
    
    public static AreaId create(){
        return new AreaId(IdentifiableProvider.generateTimeBasedUnsignedSmallInt());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        AreaId areaId1 = (AreaId) o;
        
        return areaId == areaId1.areaId;
    }
    
    @Override
    public String toString() {
        return String.valueOf(areaId);
    }
}
