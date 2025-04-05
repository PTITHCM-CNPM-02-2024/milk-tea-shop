package com.mts.backend.application.account.command;

import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdateAccountRoleCommand implements ICommand<CommandResult> {
    private AccountId id;
    private RoleId roleId;
    private boolean isActive;
    private boolean isLocked;
}
