package com.mts.backend.domain.product.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

@Value(staticConstructor = "of")
public class ProductPriceId implements Serializable {
    @Range(min = 0, max = IdentifiableProvider.INT_UNSIGNED_MAX)
    long value;
    
    public ProductPriceId(long value) {
        this.value = value;
    }
    
    public static ProductPriceId create() {
        return new ProductPriceId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }

}
