package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.GenerateRefreshTokenCommand;
import com.mts.backend.application.security.IJwtService;
import com.mts.backend.application.security.model.UserPrincipal;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class GenerateRefreshTokenCommandHandler implements ICommandHandler<GenerateRefreshTokenCommand, CommandResult> {
    IJwtService jwtService;
    @Override
    public CommandResult handle(GenerateRefreshTokenCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        
        var authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if (!(authentication instanceof UserPrincipal userPrincipal)) {
            return CommandResult.businessFail("Refresh token không hợp lệ");
        }
        
        return CommandResult.success(jwtService.generateRefreshToken(userPrincipal));
    }
    
}
