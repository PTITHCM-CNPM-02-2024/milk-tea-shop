package com.mts.backend.application.account.query;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
public class DefaultAccountQuery implements IQuery<CommandResult> {
    
}
