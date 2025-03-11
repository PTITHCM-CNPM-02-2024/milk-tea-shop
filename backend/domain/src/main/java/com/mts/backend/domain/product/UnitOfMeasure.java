package com.mts.backend.domain.product;

import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.shared.domain.AbstractAggregateRoot;

public class UnitOfMeasure extends AbstractAggregateRoot <UnitOfMeasureId> {
    /**
     * @param unitOfMeasureId
     */
    protected UnitOfMeasure(UnitOfMeasureId unitOfMeasureId) {
        super(unitOfMeasureId);
    }
}
