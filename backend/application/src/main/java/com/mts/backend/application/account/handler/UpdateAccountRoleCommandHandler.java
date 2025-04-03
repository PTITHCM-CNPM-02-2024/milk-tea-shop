package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.UpdateAccountRoleCommand;
import com.mts.backend.domain.account.AccountEntity;
import com.mts.backend.domain.account.RoleEntity;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.account.jpa.JpaRoleRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateAccountRoleCommandHandler implements ICommandHandler<UpdateAccountRoleCommand, CommandResult> {
    private final JpaAccountRepository accountRepository;
    private final JpaRoleRepository roleRepository;
    
    public UpdateAccountRoleCommandHandler(JpaAccountRepository accountRepository, JpaRoleRepository roleRepository) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
    }
    
    @Override
    @Transactional
    public CommandResult handle(UpdateAccountRoleCommand command) {
        Objects.requireNonNull(command, "UpdateAccountCommand must not be null");
        
        AccountEntity account = mustBeExist(command.getId());
        
        account.setRoleEntity(mustBeExist(command.getRoleId()));
        
        account.incrementTokenVersion();
        
        var updatedAccount = accountRepository.saveAndFlush(account);
        
        accountRepository.grantPermissionsByRole(updatedAccount.getId());
        
        return CommandResult.success(updatedAccount.getId());
        
    }
    
    private AccountEntity mustBeExist(AccountId accountId) {
        Objects.requireNonNull(accountId, "AccountId must not be null");
        return accountRepository.findById(accountId.getValue()).orElseThrow(() -> new NotFoundException("Không tìm thấy tài " +
                "khoản"));
    }
    
    private RoleEntity mustBeExist(RoleId roleId) {
        Objects.requireNonNull(roleId, "RoleId must not be null");
        
        if (!roleRepository.existsById(roleId.getValue())) {
            throw new NotFoundException("Không tìm thấy role");
        }
        
        return roleRepository.getReferenceById(roleId.getValue());
    }
}
