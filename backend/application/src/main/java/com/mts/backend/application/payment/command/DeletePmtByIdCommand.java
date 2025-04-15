package com.mts.backend.application.payment.command;

import com.mts.backend.domain.payment.identifier.PaymentMethodId;
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
public class DeletePmtByIdCommand implements ICommand<CommandResult> {
    private PaymentMethodId paymentMethodId;
}
