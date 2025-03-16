package com.mts.backend.application.staff.handler;

import com.mts.backend.application.staff.command.CreateEmployeeCommand;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.repository.IAccountRepository;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.staff.Employee;
import com.mts.backend.domain.staff.identifier.EmployeeId;
import com.mts.backend.domain.staff.repository.IEmployeeRepository;
import com.mts.backend.domain.staff.value_object.Position;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CreateEmployeeCommandHandler implements ICommandHandler<CreateEmployeeCommand, CommandResult> {
    private final  IEmployeeRepository employeeRepository;
    private final IAccountRepository accountRepository;
    
    public CreateEmployeeCommandHandler(IEmployeeRepository employeeRepository, IAccountRepository accountRepository) {
        this.employeeRepository = employeeRepository;
        this.accountRepository = accountRepository;
    }
    
    @Override
    public CommandResult handle(CreateEmployeeCommand command) {
        Employee emp = new Employee(EmployeeId.create(),
                FirstName.of(command.getFirstName()),
                LastName.of(command.getLastName()),
                Email.of(command.getEmail()),
                PhoneNumber.of(command.getPhone()),
                Position.of(command.getPosition()),
                Gender.valueOf(command.getGender()),
                AccountId.of(command.getAccountId()),
                LocalDateTime.now());
        
        verifyUniqueEmail(emp.getEmail());
        
        verifyUniquePhoneNumber(emp.getPhoneNumber());
        
        mustExitsAccountAndUniqueAccountId(emp.getAccountId());
        
        var employee = employeeRepository.save(emp);
        
        return CommandResult.success(employee.getId().getValue());
    }
    
    private void verifyUniqueEmail(Email email) {
        Objects.requireNonNull(email, "Email is required");
        if (employeeRepository.existsByEmail(email)) {
            throw new DuplicateException("Email đã tồn tại");
        }
    }
    
    private void verifyUniquePhoneNumber(PhoneNumber phoneNumber) {
        Objects.requireNonNull(phoneNumber, "Phone number is required");
        if (employeeRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DuplicateException("Số điện thoại đã tồn tại");
        }
    }
    
    private void mustExitsAccountAndUniqueAccountId(AccountId accountId) {
        Objects.requireNonNull(accountId, "Account id is required");
        if (!accountRepository.existsById(accountId)) {
            throw new NotFoundException("Tài khoản không tồn tại");
        }
        if (employeeRepository.existsByAccountId(accountId)) {
            throw new DuplicateException("Tài khoản đã được sử dụng");
        }
    }
}
