package com.mts.backend.domain.product.jpa.dto;

import com.mts.backend.domain.product.UnitOfMeasure;
import com.mts.backend.domain.product.value_object.UnitName;
import com.mts.backend.domain.product.value_object.UnitSymbol;

/**
 * Projection for {@link UnitOfMeasure}
 */
public interface UnitOfMeasureEntityInfo {
    UnitName getName();

    UnitSymbol getSymbol();
}