package com.mts.backend.domain.store.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

@Value(staticConstructor = "of")
public class AreaId {
    @Range(min = 1, max = IdentifiableProvider.SMALLINT_UNSIGNED_MAX)
    int value;
    
    public AreaId(int value) {
        this.value = value;
    }
    
    public static AreaId create(){
        return new AreaId(IdentifiableProvider.generateTimeBasedUnsignedSmallInt());
    }

}
