package com.mts.backend.application.product.query;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
@Data
@AllArgsConstructor
public class DefaultCategoryQuery implements IQuery<CommandResult> {
    private Integer page;
    private Integer size;
}
