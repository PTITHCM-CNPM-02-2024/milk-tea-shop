package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.RefreshAccessTokenCommand;
import com.mts.backend.application.account.response.AuthenticationResponse;
import com.mts.backend.application.security.IJwtService;
import com.mts.backend.application.security.model.UserPrincipal;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@AllArgsConstructor
@Service
public class RefreshAccessTokenCommandHandler implements ICommandHandler<RefreshAccessTokenCommand, CommandResult> {
    IJwtService jwtService;
    
    UserDetailsService userDetailsService;

    /**
     * @param command
     * @return
     */
    @Override
    public CommandResult handle(RefreshAccessTokenCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        
        var user = (UserPrincipal) userDetailsService.loadUserByUsername(jwtService.extractUsername(command.getRefreshToken()));

        if (user == null) {
            return CommandResult.notFoundFail("Không tìm thấy tài khoản");
        }


        if (!jwtService.validateRefreshToken(command.getRefreshToken(), user)) {
            return CommandResult.businessFail("Refresh token không hợp lệ");
        }
        
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );

        var accessToken = jwtService.generateAccessToken(user);
        var expiration =
                jwtService.extractClaim(accessToken, Claims::getExpiration).getTime() - System.currentTimeMillis();
        
        return CommandResult.success(AuthenticationResponse.builder()
                .accessToken(accessToken)
                .expiresIn(expiration)
                .build());
    }
}
