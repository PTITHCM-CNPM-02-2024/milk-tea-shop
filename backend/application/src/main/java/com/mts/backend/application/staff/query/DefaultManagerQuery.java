package com.mts.backend.application.staff.query;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import com.mts.backend.shared.query.IQueryHandler;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
public class DefaultManagerQuery implements IQuery<CommandResult> {
}
