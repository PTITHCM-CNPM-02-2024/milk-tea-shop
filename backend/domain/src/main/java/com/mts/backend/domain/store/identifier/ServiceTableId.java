package com.mts.backend.domain.store.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;

public class ServiceTableId implements Identifiable {
    
    private final int serviceTableId;
    
    private ServiceTableId(int id){
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid serviceTableId");
        }
        if (id > IdentifiableProvider.SMALLINT_UNSIGNED_MAX) {
            throw new IllegalArgumentException("Invalid serviceTableId");
        }
        
        this.serviceTableId = id;
    }
    
    public static ServiceTableId of (int id){
        return new ServiceTableId(id);
    }
    
    public static ServiceTableId of (String id){
        return new ServiceTableId(Integer.parseInt(id));
    }
    
    public static ServiceTableId create(){
        return new ServiceTableId(IdentifiableProvider.generateTimeBasedUnsignedSmallInt());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ServiceTableId that = (ServiceTableId) o;
        
        return serviceTableId == that.serviceTableId;
    }

    @Override
    public String toString() {
        return String.valueOf(serviceTableId);
    }
    
    public int getValue(){
        return serviceTableId;
    }
}
