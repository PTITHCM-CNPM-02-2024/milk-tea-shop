package com.mts.backend.domain.product.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import lombok.Value;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

@Value(staticConstructor = "of")
public class ProductId implements Serializable {
    @Range(min = 0, max = IdentifiableProvider.MEDIUMINT_UNSIGNED_MAX)
    int value;

    public ProductId(int value) {

        this.value = value;
    }

    public static ProductId create() {
        return new ProductId(IdentifiableProvider.generateTimeBasedUnsignedMediumInt());
    }
}