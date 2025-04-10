package com.mts.backend.application.order.query;

import com.mts.backend.domain.order.identifier.OrderId;
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
public class OrderByIdQuery implements IQuery<CommandResult> {
    private OrderId orderId;
}
