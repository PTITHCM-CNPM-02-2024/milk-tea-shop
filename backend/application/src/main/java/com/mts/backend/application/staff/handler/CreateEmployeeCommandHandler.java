package com.mts.backend.application.staff.handler;

import com.mts.backend.application.staff.command.CreateEmployeeCommand;
import com.mts.backend.domain.account.AccountEntity;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.staff.EmployeeEntity;
import com.mts.backend.domain.staff.identifier.EmployeeId;
import com.mts.backend.domain.staff.jpa.JpaEmployeeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CreateEmployeeCommandHandler implements ICommandHandler<CreateEmployeeCommand, CommandResult> {
    private final JpaEmployeeRepository employeeRepository;
    private final JpaAccountRepository accountRepository;
    
    public CreateEmployeeCommandHandler(JpaEmployeeRepository employeeRepository, JpaAccountRepository accountRepository) {
        this.employeeRepository = employeeRepository;
        this.accountRepository = accountRepository;
    }
    
    @Override
    @Transactional
    public CommandResult handle(CreateEmployeeCommand command) {
        
        verifyUniqueEmail(command.getEmail());
        
        verifyUniquePhoneNumber(command.getPhone());
        
        var account = mustExitsAccountAndUniqueAccountId(command.getAccountId());
        
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
    
    private AccountEntity mustExitsAccountAndUniqueAccountId(AccountId accountId) {
        Objects.requireNonNull(accountId, "Account id is required");
        if (!accountRepository.existsById(accountId.getValue())) {
            throw new NotFoundException("Tài khoản không tồn tại");
        }
        if (employeeRepository.existsByAccountId(accountId.getValue())) {
            throw new DuplicateException("Tài khoản đã được sử dụng");
        }
        
        return accountRepository.getReferenceById(accountId.getValue());
    }
}
