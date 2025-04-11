package com.mts.backend.application.product.query;

import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Data
@lombok.Builder
public class UnitByIdQuery implements com.mts.backend.shared.query.IQuery<com.mts.backend.shared.command.CommandResult> {
    private UnitOfMeasureId id;
    private Integer page;
    private Integer size;
}
