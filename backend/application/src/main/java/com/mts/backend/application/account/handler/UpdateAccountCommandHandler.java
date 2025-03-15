package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.UpdateAccountCommand;
import com.mts.backend.domain.account.Account;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.repository.IAccountRepository;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.account.value_object.Username;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateAccountCommandHandler implements ICommandHandler<UpdateAccountCommand, CommandResult> {
    private final IAccountRepository accountRepository;

    public UpdateAccountCommandHandler(IAccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
    }
    
    @Override
    public CommandResult handle(UpdateAccountCommand command) {
        Objects.requireNonNull(command, "UpdateAccountCommand must not be null");
        
        Account account = mustBeExist(AccountId.of(command.getId()));
        
        if (account.changeUserName(Username.of(command.getUsername()))) {
            verifyUniqueUsername(account.getUsername());
        }
        
        account.incrementTokenVersion();
        
        accountRepository.save(account);
        
        return CommandResult.success(account.getId().getValue());
    }
    
    private void verifyUniqueUsername(Username username) {
        if (accountRepository.existsByUsername(username)) {
            throw new DuplicateException("Tên đăng nhập đã tồn tại");
        }
    }
    
    private Account mustBeExist(AccountId accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));
    }
    
}
