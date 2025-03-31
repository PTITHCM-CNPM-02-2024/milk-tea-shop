package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.AuthenticationCommand;
import com.mts.backend.application.account.response.AuthenticationResponse;
import com.mts.backend.application.security.IJwtService;
import com.mts.backend.application.security.model.UserPrincipal;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthenticationCommandHandler implements ICommandHandler<AuthenticationCommand, CommandResult> {
    private final AuthenticationManager authenticationManager;
    private final JpaAccountRepository accountRepository;
    private final IJwtService jwtService;
    
    public AuthenticationCommandHandler(AuthenticationManager authenticationManager, JpaAccountRepository accountRepository, IJwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.accountRepository = accountRepository;
        this.jwtService = jwtService;
    }

    /**
     * @param command 
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(AuthenticationCommand command) {
        Objects.requireNonNull(command, "Command cannot be null");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        command.getUsername().getValue(),
                        command.getPassword().getValue()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        var userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        var account = accountRepository.findByUsername(command.getUsername())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản với tên đăng nhập: " + command.getUsername().getValue()));
        
        account.login();
        
        String token = jwtService.generateToken(userPrincipal);
        
        return CommandResult.success(new AuthenticationResponse(token));
    }
}
