package com.mts.backend.domain.product.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import lombok.Value;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

@Value(staticConstructor = "of")
public class ProductSizeId implements Serializable {
    @Range(min = 0, max = IdentifiableProvider.SMALLINT_UNSIGNED_MAX)
    int value;

    public ProductSizeId(int value) {
        this.value = value;
    }


    public static ProductSizeId create() {
        return new ProductSizeId(IdentifiableProvider.generateTimeBasedUnsignedSmallInt());
    }

}
