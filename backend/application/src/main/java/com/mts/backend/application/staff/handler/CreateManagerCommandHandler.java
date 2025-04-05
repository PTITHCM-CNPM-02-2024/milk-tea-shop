package com.mts.backend.application.staff.handler;

import com.mts.backend.application.staff.command.CreateManagerCommand;
import com.mts.backend.domain.account.AccountEntity;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.account.jpa.JpaRoleRepository;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.staff.ManagerEntity;
import com.mts.backend.domain.staff.identifier.ManagerId;
import com.mts.backend.domain.staff.jpa.JpaManagerRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CreateManagerCommandHandler implements ICommandHandler<CreateManagerCommand, CommandResult> {
    private final JpaManagerRepository managerRepository;
    private final JpaAccountRepository accountRepository;
    private final JpaRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    public CreateManagerCommandHandler(JpaManagerRepository managerRepository, JpaAccountRepository accountRepository, JpaRoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.managerRepository = managerRepository;
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    @Transactional
    public CommandResult handle(CreateManagerCommand command) {
        
        verifyUniqueEmail(command.getEmail());
        verifyUniquePhone(command.getPhone());
        
        var account = createAccount(command);

        ManagerEntity manager = ManagerEntity.builder()
                .id(ManagerId.create().getValue())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .email(command.getEmail())
                .phone(command.getPhone())
                .gender(command.getGender())
                .accountEntity(account)
                .build();
        
        var savedManager = managerRepository.save(manager);
        
        return  CommandResult.success(savedManager.getId());
    }
    
    private void verifyUniqueEmail (Email email){
        Objects.requireNonNull(email, "Email is required");
        if (managerRepository.existsByEmail(email)){
            throw new DuplicateException("Email đã tồn tại");
        }
        
    }
    
    private void verifyUniquePhone (PhoneNumber phoneNumber){
        Objects.requireNonNull(phoneNumber, "Phone number is required");
        if (managerRepository.existsByPhone(phoneNumber)){
            throw new DuplicateException("Số điện thoại đã tồn tại");
        }
    }
    
    private AccountEntity createAccount(CreateManagerCommand command){
        Objects.requireNonNull(command, "Account id is required");
        
        if (accountRepository.existsByUsername(command.getUsername())){
            throw new DuplicateException("Tên đăng nhập đã tồn tại");
        }
        
        if (!roleRepository.existsById(command.getRoleId().getValue())){
            throw new NotFoundException("Không tìm thấy quyền với id: " + command.getRoleId().getValue());
        }
        
        var password = PasswordHash.builder()
                .value(passwordEncoder.encode(command.getPassword().getValue()))
                .build();
        
        var account = AccountEntity.builder()
                .id(AccountId.create().getValue())
                .username(command.getUsername())
                .passwordHash(password)
                .locked(false)
                .active(false)
                .lastLogin(null)
                .tokenVersion(0L)
                .roleEntity(roleRepository.getReferenceById(command.getRoleId().getValue()))
                .build();
        
        
        accountRepository.saveAndFlush(account);
        
        accountRepository.grantPermissionsByRole(account.getId());
        
        return account;
    }
}
