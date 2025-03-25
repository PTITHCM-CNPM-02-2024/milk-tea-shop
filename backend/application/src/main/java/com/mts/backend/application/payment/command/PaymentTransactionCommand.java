package com.mts.backend.application.payment.command;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.value_object.PaymentStatus;
import com.mts.backend.domain.payment.identifier.PaymentId;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Data
@Builder
public class PaymentTransactionCommand implements ICommand<CommandResult> {
    PaymentId paymentId;
    Long transactionId;
    PaymentMethodId paymentMethodId;
    Money amount;
}
