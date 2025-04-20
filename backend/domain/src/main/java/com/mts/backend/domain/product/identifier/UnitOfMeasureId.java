package com.mts.backend.domain.product.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import lombok.Value;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;
import java.util.Objects;

@Value(staticConstructor = "of")
public class UnitOfMeasureId implements Serializable {
    @Range(min = 0, max = IdentifiableProvider.SMALLINT_UNSIGNED_MAX)
    int value;
    
    public UnitOfMeasureId(@Range(min = 0, max = IdentifiableProvider.SMALLINT_UNSIGNED_MAX) int value) {
        this.value = value;
    }

    public static UnitOfMeasureId create(){
        return new UnitOfMeasureId(IdentifiableProvider.generateTimeBasedUnsignedSmallInt());
    }

}
