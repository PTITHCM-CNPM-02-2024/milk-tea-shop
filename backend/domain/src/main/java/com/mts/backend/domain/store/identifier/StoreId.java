package com.mts.backend.domain.store.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.exception.DomainException;
import jakarta.persistence.AttributeConverter;
import lombok.Value;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

@Value(staticConstructor = "of")
public class StoreId implements Serializable {
    @Range(min = 1, max = IdentifiableProvider.TINYINT_UNSIGNED_MAX)
     int value;
    
    private StoreId(int value) {
        this.value = value;
    }
    
    public static StoreId create(){
        return new StoreId(IdentifiableProvider.generateTimeBasedUnsignedTinyInt());
    }
    
}
