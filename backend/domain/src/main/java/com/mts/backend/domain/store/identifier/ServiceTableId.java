package com.mts.backend.domain.store.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

@Value(staticConstructor = "of")
public class ServiceTableId {
    @Range(min = 1, max = IdentifiableProvider.SMALLINT_UNSIGNED_MAX)
    int value;
    
    private ServiceTableId(int id){
        this.value = id;
    }
    
    public static ServiceTableId create(){
        return new ServiceTableId(IdentifiableProvider.generateTimeBasedUnsignedSmallInt());
    }
}
