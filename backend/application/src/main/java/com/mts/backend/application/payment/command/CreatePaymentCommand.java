package com.mts.backend.application.payment.command;

import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class CreatePaymentCommand implements ICommand<CommandResult> {
    private OrderId orderId;
    private PaymentMethodId paymentMethodId;
}
