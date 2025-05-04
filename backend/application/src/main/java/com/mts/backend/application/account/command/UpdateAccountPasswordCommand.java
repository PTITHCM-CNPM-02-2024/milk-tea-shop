package com.mts.backend.application.account.command;

import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdateAccountPasswordCommand implements ICommand<CommandResult> {
    private AccountId id;
    private PasswordHash oldPassword;
    private PasswordHash newPassword;
    private PasswordHash confirmPassword;
}
