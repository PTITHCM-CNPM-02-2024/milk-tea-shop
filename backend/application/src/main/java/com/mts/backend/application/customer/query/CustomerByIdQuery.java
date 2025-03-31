package com.mts.backend.application.customer.query;

import com.mts.backend.domain.customer.identifier.CustomerId;
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
public class CustomerByIdQuery implements IQuery<CommandResult> {
    private CustomerId id;
}
