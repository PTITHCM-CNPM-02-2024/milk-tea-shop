package com.mts.backend.application.staff.handler;

import com.mts.backend.application.staff.command.CreateManagerCommand;
import com.mts.backend.domain.account.Account;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.account.jpa.JpaRoleRepository;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.staff.Manager;
import com.mts.backend.domain.staff.identifier.ManagerId;
import com.mts.backend.domain.staff.jpa.JpaManagerRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
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
        Objects.requireNonNull(command, "CreateManagerCommand is required");
        
        var account = createAccount(command);

        try{
            Manager manager = Manager.builder()
                .id(ManagerId.create().getValue())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .email(command.getEmail())
                .phone(command.getPhone())
                .gender(command.getGender())
                .account(account)
                .build();
        
        var savedManager = managerRepository.save(manager);
        
        return  CommandResult.success(savedManager.getId());
        }catch(DataIntegrityViolationException e){
            if(e.getMessage().contains("fk_manager_account")){
                throw new NotFoundException("Tài khoản không tồn tại");
            }
            if(e.getMessage().contains("uk_manager_email")){
                throw new DuplicateException("Email đã tồn tại");
            }
            if(e.getMessage().contains("uk_manager_phone")){
                throw new DuplicateException("Số điện thoại đã tồn tại");
            }
            throw new DomainException("Lỗi khi tạo nhân viên", e);
        }
    
    }
    
    private Account createAccount(CreateManagerCommand command){
        Objects.requireNonNull(command, "Account id is required");
        
        try{
            var password = PasswordHash.of(passwordEncoder.encode(command.getPassword().getValue()));
        
        var account = Account.builder()
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

        }catch(DataIntegrityViolationException e){
            if(e.getMessage().contains("uk_account_username")){
                throw new DuplicateException("Tên đăng nhập đã tồn tại");
            }
            if(e.getMessage().contains("fk_account_role")){
                throw new NotFoundException("Quyền không tồn tại");
            }
            throw new DomainException("Lỗi khi tạo tài khoản", e);
        }
        
    }
}
