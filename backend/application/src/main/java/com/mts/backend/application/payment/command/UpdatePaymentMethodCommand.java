package com.mts.backend.application.payment.command;

import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.domain.payment.value_object.PaymentMethodName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UpdatePaymentMethodCommand implements ICommand<CommandResult> {
    private PaymentMethodId paymentMethodId;
    private PaymentMethodName name;
    private String description;
    
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
}
