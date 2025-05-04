package com.mts.backend.application.account.command;

import com.mts.backend.application.security.model.UserPrincipal;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class RefreshAccessTokenCommand implements ICommand<CommandResult> {
    private String refreshToken;
}
