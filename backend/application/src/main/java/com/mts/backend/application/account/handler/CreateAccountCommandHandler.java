package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.CreateAccountCommand;
import com.mts.backend.domain.account.Account;
import com.mts.backend.domain.account.Role;
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

import org.springframework.dao.DataIntegrityViolationException;
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
    
        try{
            PasswordHash passwordHash = encodePassword(command.getPassword());
        
        
            Account account = Account.builder()
                    .id(AccountId.create().getValue())
                    .username(command.getUsername())
                    .passwordHash(passwordHash)
                    .roleEntity(roleRepository.getReferenceById(command.getRoleId().getValue()))
                    .active(false)
                    .tokenVersion(0L)
                    .locked(false)
                    .build();
            
            
            var savedAccount = accountRepository.saveAndFlush(account);
            
            accountRepository.grantPermissionsByRole(savedAccount.getId());
            
            
            return CommandResult.success(savedAccount.getId());
        }catch(DataIntegrityViolationException e){
            if(e.getMessage().contains("uk_account_username")){
                throw new DuplicateException("Tên đăng nhập đã tồn tại");
            }
            if(e.getMessage().contains("fk_account_role_id")){
                throw new NotFoundException("Quyền truy cập không tồn tại");
            }
            throw e;
        }
    }
    
    private PasswordHash encodePassword(PasswordHash password) {
        return PasswordHash.of(passwordEncoder.encode(password.getValue()));
    }
}
