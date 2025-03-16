package com.mts.backend.application.staff.handler;

import com.mts.backend.application.staff.command.UpdateEmployeeCommand;
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

import java.util.Objects;

@Service
public class UpdateEmployeeCommandHandler implements ICommandHandler<UpdateEmployeeCommand, CommandResult> {
    private final IEmployeeRepository employeeRepository;
    private final IAccountRepository accountRepository;
    
    public UpdateEmployeeCommandHandler(IEmployeeRepository employeeRepository, IAccountRepository accountRepository) {
        this.employeeRepository = employeeRepository;
        this.accountRepository = accountRepository;
    }
    @Override
    public CommandResult handle(UpdateEmployeeCommand command) {
        Objects.requireNonNull(command, "Update employee command is required");
        
        Employee employee = mustExistEmployee(EmployeeId.of(command.getId()));
        
        if (employee.changeEmail(Email.of(command.getEmail()))) {
            verifyUniqueEmail(employee.getEmail());
        }
        if (employee.changePhoneNumber(PhoneNumber.of(command.getPhone()))) {
            verifyUniquePhoneNumber(employee.getPhoneNumber());
        }
        
        mustExitsAccount(employee.getAccountId());
        
        employee.changePosition(Position.of(command.getPosition()));
        employee.changeFirstName(FirstName.of(command.getFirstName()));
        employee.changeLastName(LastName.of(command.getLastName()));
        employee.changeGender(Gender.valueOf(command.getGender()));
        
        var updatedEmployee = employeeRepository.save(employee);
        
        return CommandResult.success(updatedEmployee.getId().getValue());
    }
    
    private Employee mustExistEmployee(EmployeeId employeeId) {
        Objects.requireNonNull(employeeId, "Employee id is required");
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Nhân viên không tồn tại"));
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
    
    private void mustExitsAccount(AccountId accountId) {
        Objects.requireNonNull(accountId, "Account id is required");
        if (!accountRepository.existsById(accountId)) {
            throw new DuplicateException("Account không tồn tại");
        }
        
    }
}
