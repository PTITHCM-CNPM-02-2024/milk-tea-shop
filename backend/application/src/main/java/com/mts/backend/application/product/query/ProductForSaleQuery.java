package com.mts.backend.application.product.query;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class ProductForSaleQuery implements IQuery<CommandResult> {
    protected Boolean availableOrder;
    protected Integer size;
    protected Integer page;
}
