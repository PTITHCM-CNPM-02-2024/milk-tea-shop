package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.CreateAccountCommand;
import com.mts.backend.domain.account.AccountEntity;
import com.mts.backend.domain.account.RoleEntity;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.account.jpa.JpaRoleRepository;
import com.mts.backend.domain.account.value_object.PasswordHash;
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
public class CreateAccountCommandHandler implements ICommandHandler<CreateAccountCommand, CommandResult> {
    
    private final JpaAccountRepository accountRepository;
    private final JpaRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    public CreateAccountCommandHandler(JpaAccountRepository accountRepository, JpaRoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(CreateAccountCommand command) {
        Objects.requireNonNull(command, "CreateAccountCommand must not be null");
        
        PasswordHash passwordHash = encodePassword(command.getPassword());
        
        verifyUniqueUsername(command.getUsername());
        
        AccountEntity account = AccountEntity.builder()
                .id(AccountId.create().getValue())
                .username(command.getUsername())
                .passwordHash(passwordHash)
                .roleEntity(verifyRole(command.getRoleId()))
                .active(false)
                .tokenVersion(0L)
                .locked(false)
                .build();
        
        
        var savedAccount = accountRepository.save(account);
        
        return CommandResult.success(savedAccount.getId());
    }
    
    private void verifyUniqueUsername(Username username) {
        Objects.requireNonNull(username, "Username is required");
        
        if (accountRepository.existsByUsername(username)){
            throw new DuplicateException("Tên đăng nhập đã tồn tại");
        }
    }
    
    private RoleEntity verifyRole(RoleId id){
        Objects.requireNonNull(id, "Role id is required");
        
        if (!roleRepository.existsById(id.getValue())){
            throw new NotFoundException("Không tìm thấy quyền truy cập");
        }
        
        return roleRepository.getReferenceById(id.getValue());
    }
    
    private PasswordHash encodePassword(PasswordHash password) {
        return PasswordHash.builder().value(passwordEncoder.encode(password.getValue())).build();
    }
}
