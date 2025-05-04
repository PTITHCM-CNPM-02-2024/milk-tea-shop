package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.LogoutCommand;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LogoutCommandHandler implements ICommandHandler<LogoutCommand, CommandResult> {
    
    final JpaAccountRepository accountRepository;
    /**
     * @param command 
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(LogoutCommand command) {
        Objects.requireNonNull(command, "Command cannot be null");
        
        var account = accountRepository.findById(command.getUserPrincipal().getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản với id: " + command.getUserPrincipal().getId()));
        
        account.logout();
        
        SecurityContextHolder.clearContext();
        
        return CommandResult.success(account.getId());
    }
}
