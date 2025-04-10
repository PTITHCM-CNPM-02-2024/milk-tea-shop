package com.mts.backend.application.payment.query;

import com.mts.backend.domain.payment.identifier.PaymentId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
public class DefaultPaymentQuery implements IQuery<CommandResult> {
    private Integer page;
    private Integer size;
}
