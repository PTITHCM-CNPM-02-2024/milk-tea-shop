package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.UpdateAccountRoleCommand;
import com.mts.backend.domain.account.Account;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.repository.IAccountRepository;
import com.mts.backend.domain.account.repository.IRoleRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateAccountRoleCommandHandler implements ICommandHandler<UpdateAccountRoleCommand, CommandResult> {
    private final IAccountRepository accountRepository;
    private final IRoleRepository roleRepository;
    
    public UpdateAccountRoleCommandHandler(IAccountRepository accountRepository, IRoleRepository roleRepository) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
    }
    
    @Override
    public CommandResult handle(UpdateAccountRoleCommand command) {
        Objects.requireNonNull(command, "UpdateAccountCommand must not be null");
        
        Account account = mustBeExist(AccountId.of(command.getId()));
        
        mustBeExist(RoleId.of(command.getRoleId()));
        
        account.changeRole(RoleId.of(command.getRoleId()));
        
        account.incrementTokenVersion();
        
        var updatedAccount = accountRepository.save(account);
        
        return CommandResult.success(updatedAccount.getId().getValue());
        
    }
    
    private Account mustBeExist(AccountId accountId) {
        Objects.requireNonNull(accountId, "AccountId must not be null");
        return accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));
    }
    
    private void  mustBeExist(RoleId roleId) {
        Objects.requireNonNull(roleId, "RoleId must not be null");
        
        if (!roleRepository.existsById(roleId)) {
            throw new NotFoundException("Không tìm thấy role");
        }
    }
}
