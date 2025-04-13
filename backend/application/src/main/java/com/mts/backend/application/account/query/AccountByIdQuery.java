package com.mts.backend.application.account.query;

import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class AccountByIdQuery implements IQuery<CommandResult> {
    
    private AccountId id;
}
