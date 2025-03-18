package com.mts.backend.application.store.query;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
public class  DefaultAreaQuery  implements IQuery<CommandResult> {
}
