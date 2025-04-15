package com.mts.backend.application.staff.query;

import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GetEmpByAccountIdQuery implements IQuery<CommandResult> {
    private AccountId accountId;
}
