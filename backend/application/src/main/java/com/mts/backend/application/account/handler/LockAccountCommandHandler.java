package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.UpdateLockAccountCommand;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class LockAccountCommandHandler implements ICommandHandler<UpdateLockAccountCommand, CommandResult> {
    
    private final JpaAccountRepository accountRepository;
    
    public LockAccountCommandHandler(JpaAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * @param command 
     * @return
     */
    @Override
    public CommandResult handle(UpdateLockAccountCommand command) {
        Objects.requireNonNull(command, "UpdateLockAccountCommand must not be null");
        
        var account = accountRepository.findById(command.getId().getValue())
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản không tồn tại"));
        
        account.changeLock(command.isLocked());
        
        var updatedAccount = accountRepository.saveAndFlush(account);
        
        accountRepository.lockUnlockAccount(updatedAccount.getId(), updatedAccount.getLocked());
        
        return CommandResult.success(updatedAccount.getId());
    }
    
    
}
