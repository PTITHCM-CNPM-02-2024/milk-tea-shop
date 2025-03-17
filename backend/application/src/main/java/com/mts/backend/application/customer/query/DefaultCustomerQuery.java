package com.mts.backend.application.customer.query;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
public class DefaultCustomerQuery implements IQuery<CommandResult> {
}
