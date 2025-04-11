package com.mts.backend.application.product.query;

import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor@Data
@Builder
public class CatByIdQuery implements IQuery<CommandResult> {
    private CategoryId id;
}
