package com.mts.backend.domain.product.jpa.dto;

import com.mts.backend.domain.product.ProductSize;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.value_object.ProductSizeName;
import com.mts.backend.domain.product.value_object.QuantityOfProductSize;

/**
 * Projection for {@link ProductSize}
 */
public interface ProductSizeEntityInfo {
    ProductSizeId getId();

    ProductSizeName getName();

    QuantityOfProductSize getQuantity();

    String getDescription();

    UnitOfMeasureEntityInfo getUnit();
}