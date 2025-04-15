package com.mts.backend.application.payment.query;

import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class PaymentMethodByIdQuery implements IQuery<CommandResult> {
    private PaymentMethodId paymentMethodId;

}
