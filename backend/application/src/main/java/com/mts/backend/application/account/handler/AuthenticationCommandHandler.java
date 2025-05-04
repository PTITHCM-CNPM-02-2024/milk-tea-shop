package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.AuthenticationCommand;
import com.mts.backend.application.account.response.AuthenticationResponse;
import com.mts.backend.application.security.IJwtService;
import com.mts.backend.application.security.model.UserPrincipal;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Profile("dev")
public class AuthenticationCommandHandler implements ICommandHandler<AuthenticationCommand, CommandResult> {
    private final AuthenticationManager authenticationManager;
    private final JpaAccountRepository accountRepository;
    private final IJwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationCommandHandler(AuthenticationManager authenticationManager, JpaAccountRepository accountRepository, IJwtService jwtService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.accountRepository = accountRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @param command
     * @return
     */
    @Override
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


        var accessToken = jwtService.generateAccessToken(userPrincipal);
        var expiration = jwtService.extractClaim(accessToken, Claims::getExpiration);
        var account = userPrincipal.getId();
        
        var response = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .expiresIn(expiration.getTime() - System.currentTimeMillis())
                .id(account)
                .tokenType("Bearer")
                .build();

        return CommandResult.success(response);
    }
}
