package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.UpdateAccountPasswordCommand;
import com.mts.backend.application.account.response.AuthenticationResponse;
import com.mts.backend.application.security.IJwtService;
import com.mts.backend.application.security.model.UserPrincipal;
import com.mts.backend.domain.account.Account;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.NotFoundException;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class UpdateAccountPasswordCommandHandler implements ICommandHandler<UpdateAccountPasswordCommand, CommandResult> {
    JpaAccountRepository accountRepository;
     PasswordEncoder  passwordEncoder;
     IJwtService jwtService;
    /**
     * @param command 
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(UpdateAccountPasswordCommand command) {
        Objects.requireNonNull(command, "UpdateAccountPassword must not be null");
        
        Account account = mustExistAccount(command.getId());
        
        PasswordHash newPasswordHash = encodePassword(command.getNewPassword());
        
        if (!passwordEncoder.matches(command.getConfirmPassword().getValue(), newPasswordHash.getValue())) {
            throw new DomainException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }
        
        if (!passwordEncoder.matches(command.getOldPassword().getValue(), account.getPasswordHash().getValue())) {
            throw new DomainException("Mật khẩu cũ không chính xác");
        }
        
        if (passwordEncoder.matches(command.getNewPassword().getValue(), account.getPasswordHash().getValue())) {
            throw new DomainException("Mật khẩu mới không được trùng với mật khẩu cũ");
        }
        
        account.setPasswordHash(newPasswordHash);
        
        account.incrementTokenVersion();
        
        accountRepository.saveAndFlush(account);
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                account.getUsername().getValue(),
                command.getNewPassword().getValue()
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        var userPrincipal = (UserPrincipal) authentication.getPrincipal();
        var accessToken = jwtService.generateAccessToken(userPrincipal);
        var expiration = jwtService.extractClaim(accessToken, Claims::getExpiration);
        var accountId = userPrincipal.getId();

        return CommandResult.success(
                AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .expiresIn(expiration.getTime() - System.currentTimeMillis())
                        .id(accountId)
                        .build()
        );
        
        
    }
    
    private Account mustExistAccount(AccountId accountId) {
        return accountRepository.findById(accountId.getValue())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));
    }
    
    private PasswordHash encodePassword(PasswordHash passwordHash) {
        return PasswordHash.of(passwordEncoder.encode(passwordHash.getValue()));
    }
    
    
}
