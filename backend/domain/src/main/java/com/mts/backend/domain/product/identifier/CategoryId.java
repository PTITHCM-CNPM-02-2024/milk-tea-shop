package com.mts.backend.domain.product.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;
import java.util.Objects;

@Value(staticConstructor = "of")
public class CategoryId implements Serializable {
    @Range(min = 0, max = IdentifiableProvider.SMALLINT_UNSIGNED_MAX)
    int value;

    public CategoryId(int value) {

        this.value = value;
    }
    
    public static CategoryId create() {
        return new CategoryId(IdentifiableProvider.generateTimeBasedUnsignedSmallInt());
    }

}
