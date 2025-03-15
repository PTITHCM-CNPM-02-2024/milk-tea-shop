package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.CreateAccountCommand;
import com.mts.backend.domain.account.Account;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.repository.IAccountRepository;
import com.mts.backend.domain.account.repository.IRoleRepository;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.account.value_object.Username;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CreateAccountCommandHandler implements ICommandHandler<CreateAccountCommand, CommandResult> {
    
    private final IAccountRepository accountRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    public CreateAccountCommandHandler(IAccountRepository accountRepository, IRoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    public CommandResult handle(CreateAccountCommand command) {
        Objects.requireNonNull(command, "CreateAccountCommand must not be null");
        
        PasswordHash passwordHash = encodePassword(PasswordHash.of(command.getPassword()));
        
        Account account = new Account(
                AccountId.create(),
                Username.of(command.getUsername()),
                passwordHash,
                RoleId.of(command.getRoleId()),
                null,
                0L,
                true,
                false,
                LocalDateTime.now());
        
        verifyUniqueUsername(account.getUsername());
        
        mustExistRoleId(RoleId.of(command.getRoleId()));
        
        var savedAccount = accountRepository.save(account);
        
        return CommandResult.success(savedAccount.getId().getValue());
    }
    
    private void verifyUniqueUsername(Username username) {
        Objects.requireNonNull(username, "Username is required");
        
        if (accountRepository.existsByUsername(username)){
            throw new DuplicateException("Tên đăng nhập đã tồn tại");
        }
    }
    
    private void mustExistRoleId(RoleId roleId) {
        Objects.requireNonNull(roleId, "Role id is required");

        if (!roleRepository.existsById(roleId))
            throw new NotFoundException("Role id not found");
    
    }
    
    private PasswordHash encodePassword(PasswordHash password) {
        return PasswordHash.of(passwordEncoder.encode(password.getValue()));
    }
}
