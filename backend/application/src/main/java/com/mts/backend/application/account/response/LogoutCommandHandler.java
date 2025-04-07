package com.mts.backend.application.account.response;

import com.mts.backend.application.account.handler.LogoutCommand;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class LogoutCommandHandler implements ICommandHandler<LogoutCommand, CommandResult> {
    
    private final JpaAccountRepository accountRepository;
    
    public LogoutCommandHandler(JpaAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * @param command 
     * @return
     */
    @Override
    public CommandResult handle(LogoutCommand command) {
        Objects.requireNonNull(command, "Command cannot be null");
        
        var account = accountRepository.findById(command.getUserPrincipal().getId().getValue())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản với id: " + command.getUserPrincipal().getId().getValue()));
        
        account.logout();
        
        return CommandResult.success(null);
    }
}
