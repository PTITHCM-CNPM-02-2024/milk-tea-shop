package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.UpdateAccountPasswordCommand;
import com.mts.backend.domain.account.Account;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.repository.IAccountRepository;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateAccountPasswordCommandHandler implements ICommandHandler<UpdateAccountPasswordCommand, CommandResult> {
    private final IAccountRepository accountRepository;
    private final PasswordEncoder  passwordEncoder;
    
    public UpdateAccountPasswordCommandHandler(IAccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    public CommandResult handle(UpdateAccountPasswordCommand command) {
        Objects.requireNonNull(command, "UpdateAccountPassword must not be null");
        
        Account account = mustExistAccount(AccountId.of(command.getId()));
        
        PasswordHash newPasswordHash = encodePassword(PasswordHash.of(command.getNewPassword()));
        
        if (!passwordEncoder.matches(command.getConfirmPassword(), newPasswordHash.getValue())) {
            throw new DomainException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }
        
        if (!account.changePassword(newPasswordHash)) {
            throw new DomainException("Mật khẩu mới không được trùng với mật khẩu cũ");
        }
        
        account.incrementTokenVersion();
        
        var savedAccount = accountRepository.save(account);
        
        return CommandResult.success(savedAccount.getId().getValue());
    }
    
    private Account mustExistAccount(AccountId accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));
    }
    
    private PasswordHash encodePassword(PasswordHash passwordHash) {
        return PasswordHash.of(passwordEncoder.encode(passwordHash.getValue()));
    }
    
    
}
