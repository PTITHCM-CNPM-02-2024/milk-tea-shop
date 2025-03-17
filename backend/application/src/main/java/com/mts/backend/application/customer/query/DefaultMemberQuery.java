package com.mts.backend.application.customer.query;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
public class DefaultMemberQuery implements IQuery<CommandResult> {
}
