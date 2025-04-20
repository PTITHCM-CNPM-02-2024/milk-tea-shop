package com.mts.backend.domain.product.jpa.dto;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.product.ProductPrice;
import com.mts.backend.domain.product.identifier.ProductPriceId;

/**
 * Projection for {@link ProductPrice}
 */
public interface ProductPriceEntityInfo {
    ProductPriceId getId();

    Money getPrice();

    ProductSizeEntityInfo getSize();
}