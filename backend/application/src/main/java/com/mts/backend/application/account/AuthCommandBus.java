package com.mts.backend.application.account;

import com.mts.backend.application.account.command.AuthenticationCommand;
import com.mts.backend.application.account.command.GenerateRefreshTokenCommand;
import com.mts.backend.application.account.command.LogoutCommand;
import com.mts.backend.application.account.command.RefreshAccessTokenCommand;
import com.mts.backend.application.account.handler.AuthenticationCommandHandler;
import com.mts.backend.application.account.handler.GenerateRefreshTokenCommandHandler;
import com.mts.backend.application.account.handler.LogoutCommandHandler;
import com.mts.backend.application.account.handler.RefreshAccessTokenCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class AuthCommandBus extends AbstractCommandBus {
    public AuthCommandBus(
            AuthenticationCommandHandler authenticationCommandHandler,
            RefreshAccessTokenCommandHandler refreshAccessTokenCommandHandler,
            GenerateRefreshTokenCommandHandler generateRefreshTokenCommandHandler,
            LogoutCommandHandler logoutCommandHandler) {
        register(AuthenticationCommand.class, authenticationCommandHandler);
        register(LogoutCommand.class, logoutCommandHandler);
        register(RefreshAccessTokenCommand.class, refreshAccessTokenCommandHandler);
        register(GenerateRefreshTokenCommand.class, generateRefreshTokenCommandHandler);
    }
}
