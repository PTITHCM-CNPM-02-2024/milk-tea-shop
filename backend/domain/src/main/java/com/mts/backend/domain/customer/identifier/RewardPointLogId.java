package com.mts.backend.domain.customer.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;

public class RewardPointLogId implements Identifiable {
    
    private final long id;
    
    private RewardPointLogId(long id) {
        
        if (id <= 0) {
            throw new IllegalArgumentException("RewardPointLog id must be greater than 0");
        }
        
        if (id > IdentifiableProvider.INT_UNSIGNED_MAX)
            throw new IllegalArgumentException("RewardPointLog id must be less than " + IdentifiableProvider.INT_UNSIGNED_MAX);
        this.id = id;
    }
    
    public static RewardPointLogId of(long id) {
        return new RewardPointLogId(id);
    }
    
    public static RewardPointLogId of(String id) {
        return new RewardPointLogId(Long.parseLong(id));
    }
    
    public static RewardPointLogId create(){
        return new RewardPointLogId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
    
    public long getValue() {
        return id;
    }
    
    @Override
    public String toString() {
        return String.valueOf(id);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RewardPointLogId rewardPointLogId)) return false;
        return id == rewardPointLogId.id;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
    
    
}
