package com.mts.backend.application.billing.command;

import com.mts.backend.domain.order.Order;
import com.mts.backend.domain.payment.Payment;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CreateInvoiceCommand implements ICommand<CommandResult> {
    
    private Order order;
    private Payment payment;
}
