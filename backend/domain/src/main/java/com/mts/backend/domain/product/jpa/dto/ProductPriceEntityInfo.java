package com.mts.backend.domain.product.jpa.dto;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.product.identifier.ProductPriceId;

/**
 * Projection for {@link com.mts.backend.domain.product.ProductPriceEntity}
 */
public interface ProductPriceEntityInfo {
    ProductPriceId getId();

    Money getPrice();

    ProductSizeEntityInfo getSize();
}