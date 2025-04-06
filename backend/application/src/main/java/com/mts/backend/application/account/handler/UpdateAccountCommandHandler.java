package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.UpdateAccountCommand;
import com.mts.backend.domain.account.AccountEntity;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.account.value_object.Username;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateAccountCommandHandler implements ICommandHandler<UpdateAccountCommand, CommandResult> {
    private final JpaAccountRepository accountRepository;

    public UpdateAccountCommandHandler(JpaAccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
    }
    
    @Override
    @Transactional
    public CommandResult handle(UpdateAccountCommand command) {
        Objects.requireNonNull(command, "UpdateAccountCommand must not be null");
        
        AccountEntity account = mustBeExist(command.getId());
        
        if (account.changeUserName(command.getUsername())) {
            verifyUniqueUsername(command.getId(), command.getUsername());
        }
        
        account.incrementTokenVersion();
        
        return CommandResult.success(account.getId());
    }
    
    private void verifyUniqueUsername(AccountId id, Username username) {
        if (accountRepository.existsByIdNotAndUsername(id.getValue(), username)) {
            throw new DuplicateException("Tên đăng nhập đã tồn tại");
        }
    }
    
    private AccountEntity mustBeExist(AccountId accountId) {
        return accountRepository.findById(accountId.getValue()).orElseThrow(() -> new NotFoundException("Không tìm thấy tài " +
                "khoản"));
    }
    
}
