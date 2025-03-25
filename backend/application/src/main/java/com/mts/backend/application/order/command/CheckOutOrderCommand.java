package com.mts.backend.application.order.command;

import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CheckOutOrderCommand implements ICommand<CommandResult> {
    private OrderId orderId;
}
