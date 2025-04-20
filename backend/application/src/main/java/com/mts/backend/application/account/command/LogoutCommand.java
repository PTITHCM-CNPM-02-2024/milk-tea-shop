package com.mts.backend.application.account.command;

import com.mts.backend.application.security.model.UserPrincipal;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LogoutCommand implements ICommand<CommandResult> {
    private UserPrincipal userPrincipal;
}
