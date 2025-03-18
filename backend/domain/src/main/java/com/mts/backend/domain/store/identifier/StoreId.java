package com.mts.backend.domain.store.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;


public class StoreId implements Identifiable {
    private final int storeId;
    
    private StoreId(int storeId) {
        if (storeId <= 0) {
            throw new IllegalArgumentException("Invalid storeId");
        }
        if (storeId > IdentifiableProvider.TINYINT_UNSIGNED_MAX) {
            throw new IllegalArgumentException("Invalid storeId");
        }
        this.storeId = storeId;
    }
    
    public static StoreId of(int storeId) {
        return new StoreId(storeId);
    }
    
    public static StoreId of (String storeId) {
        return new StoreId(Integer.parseInt(storeId));
    }
    
    
    public int getValue() {
        return storeId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        StoreId storeId1 = (StoreId) o;
        
        return storeId == storeId1.storeId;
    }
    
    
    public static StoreId create(){
        return new StoreId(IdentifiableProvider.generateTimeBasedUnsignedTinyInt());
    }
    
}
