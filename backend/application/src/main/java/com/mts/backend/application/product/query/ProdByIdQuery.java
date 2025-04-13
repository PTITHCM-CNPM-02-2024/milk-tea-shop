package com.mts.backend.application.product.query;

import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProdByIdQuery implements IQuery<CommandResult> {
    private ProductId id;
}
