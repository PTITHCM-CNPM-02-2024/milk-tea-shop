package com.mts.backend.application.staff.handler;

import com.mts.backend.application.staff.command.CreateEmployeeCommand;
import com.mts.backend.domain.account.AccountEntity;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.account.jpa.JpaRoleRepository;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.staff.EmployeeEntity;
import com.mts.backend.domain.staff.identifier.EmployeeId;
import com.mts.backend.domain.staff.jpa.JpaEmployeeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CreateEmployeeCommandHandler implements ICommandHandler<CreateEmployeeCommand, CommandResult> {
    private final JpaEmployeeRepository employeeRepository;
    private final JpaAccountRepository accountRepository;
    private final JpaRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    public CreateEmployeeCommandHandler(JpaEmployeeRepository employeeRepository, JpaAccountRepository accountRepository, JpaRoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    @Transactional
    public CommandResult handle(CreateEmployeeCommand command) {
        
        verifyUniqueEmail(command.getEmail());
        
        verifyUniquePhoneNumber(command.getPhone());
        
        var account = createAccount(command);
        
        var em = EmployeeEntity.
                builder()
                .id(EmployeeId.create().getValue())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .phone(command.getPhone())
                .email(command.getEmail())
                .position(command.getPosition())
                .gender(command.getGender())
                .accountEntity(account)
                .build();
        
        var result = employeeRepository.save(em); 
        return CommandResult.success(em.getId());
    }
    
    private void verifyUniqueEmail(Email email) {
        Objects.requireNonNull(email, "Email is required");
        if (employeeRepository.existsByEmail(email)) {
            throw new DuplicateException("Email đã tồn tại");
        }
    }
    
    private void verifyUniquePhoneNumber(PhoneNumber phoneNumber) {
        Objects.requireNonNull(phoneNumber, "Phone number is required");
        if (employeeRepository.existsByPhone(phoneNumber)) {
            throw new DuplicateException("Số điện thoại đã tồn tại");
        }
    }
    
    private AccountEntity createAccount(CreateEmployeeCommand command){
        
        if (accountRepository.existsByUsername(command.getUsername())){
            throw new DuplicateException("Tài khoản " + command.getUsername() + " đã tồn tại");
        }
        
        if (!roleRepository.existsById(command.getRoleId().getValue())){
            throw new NotFoundException("Vai trò " + command.getRoleId().getValue() + " không tồn tại");
        }
        
        var password = PasswordHash.builder().value(passwordEncoder.encode(command.getPassword().getValue())).build();

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
