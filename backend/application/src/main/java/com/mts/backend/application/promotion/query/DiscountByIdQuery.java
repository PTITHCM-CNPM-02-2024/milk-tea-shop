package com.mts.backend.application.promotion.query;

import com.mts.backend.domain.promotion.identifier.DiscountId;
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
public class DiscountByIdQuery implements IQuery<CommandResult> {
    private DiscountId id;
}
