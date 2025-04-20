package com.mts.backend.application.account;

import com.mts.backend.application.account.command.AuthenticationCommand;
import com.mts.backend.application.account.command.LogoutCommand;
import com.mts.backend.application.account.handler.AuthenticationCommandHandler;
import com.mts.backend.application.account.handler.LogoutCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class AuthCommandBus extends AbstractCommandBus {
    public AuthCommandBus(
            AuthenticationCommandHandler authenticationCommandHandler,
            LogoutCommandHandler logoutCommandHandler) {
        register(AuthenticationCommand.class, authenticationCommandHandler);
        register(LogoutCommand.class, logoutCommandHandler);
    }
}
