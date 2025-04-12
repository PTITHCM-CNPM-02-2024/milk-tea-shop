package com.mts.backend.application.promotion.query;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class UnusedCouponQuery implements IQuery<CommandResult> {
    private Integer page;
    private Integer size;
}
