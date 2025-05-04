package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.UpdateAccountCommand;
import com.mts.backend.application.account.response.AuthenticationResponse;
import com.mts.backend.application.security.IJwtService;
import com.mts.backend.application.security.model.UserPrincipal;
import com.mts.backend.domain.account.Account;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.account.value_object.Username;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class UpdateAccountCommandHandler implements ICommandHandler<UpdateAccountCommand, CommandResult> {
    JpaAccountRepository accountRepository;
    IJwtService jwtService;
    AuthenticationManager authenticationManager;
    @Override
    @Transactional
    public CommandResult handle(UpdateAccountCommand command) {
        Objects.requireNonNull(command, "UpdateAccountCommand must not be null");
        
        try{
            Account account = mustBeExist(command.getId());
            
            account.setUsername(command.getUsername());
            
            account.incrementTokenVersion();
            accountRepository.saveAndFlush(account);
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            account.getUsername().getValue(),
                            account.getPassword().getValue()
                    )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            var userPrincipal = (UserPrincipal) authentication.getPrincipal();
            var accessToken = jwtService.generateAccessToken(userPrincipal);
            var expiration = jwtService.extractClaim(accessToken, Claims::getExpiration);
            var accountId = userPrincipal.getId();
            
            var response = AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .expiresIn(expiration.getTime() - System.currentTimeMillis())
                    .id(accountId)
                    .build();
            
            return CommandResult.success(response);
        }catch(DataIntegrityViolationException e){
            if(e.getMessage().contains("uk_account_username")){
                throw new DuplicateException("Tên đăng nhập đã tồn tại");
            }
            throw e;
        }
    }
    
    
    private Account mustBeExist(AccountId accountId) {
        return accountRepository.findById(accountId.getValue()).orElseThrow(() -> new NotFoundException("Không tìm thấy tài " +
                "khoản"));
    }
    
}
