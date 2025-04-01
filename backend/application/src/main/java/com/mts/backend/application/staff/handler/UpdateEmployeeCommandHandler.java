package com.mts.backend.application.staff.handler;

import com.mts.backend.application.staff.command.UpdateEmployeeCommand;
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
public class UpdateEmployeeCommandHandler implements ICommandHandler<UpdateEmployeeCommand, CommandResult> {
    private final JpaEmployeeRepository employeeRepository;
    private final JpaAccountRepository accountRepository;
    
    public UpdateEmployeeCommandHandler(JpaEmployeeRepository employeeRepository, JpaAccountRepository accountRepository) {
        this.employeeRepository = employeeRepository;
        this.accountRepository = accountRepository;
    }
    @Override
    @Transactional
    public CommandResult handle(UpdateEmployeeCommand command) {
        Objects.requireNonNull(command, "Update employee command is required");
        
        EmployeeEntity employee = mustExistEmployee(command.getId());
        
        if (employee.changeEmail(command.getEmail())) {
            verifyUniqueEmail(command.getId(), command.getEmail());
        }
        if (employee.changePhoneNumber(command.getPhone())) {
            verifyUniquePhoneNumber(command.getId(), command.getPhone());
        }
        
        employee.changePosition(command.getPosition());
        employee.changeFirstName(command.getFirstName());
        employee.changeLastName(command.getLastName());
        employee.changeGender(command.getGender());
        
        
        return CommandResult.success(employee.getId());
    }
    
    private EmployeeEntity mustExistEmployee(EmployeeId employeeId) {
        Objects.requireNonNull(employeeId, "Employee id is required");
        return employeeRepository.findById(employeeId.getValue())
                .orElseThrow(() -> new NotFoundException("Nhân viên không tồn tại"));
    }
    
    private void verifyUniqueEmail(EmployeeId id,  Email email) {
        Objects.requireNonNull(email, "Email is required");
        if (employeeRepository.existsByIdNotAndEmail(id.getValue(), email)) {
            throw new DuplicateException("Email đã tồn tại");
        }
    }
    
    private void verifyUniquePhoneNumber(EmployeeId id,  PhoneNumber phone) {
        Objects.requireNonNull(phone, "Phone number is required");
        if (employeeRepository.existsByIdNotAndPhone(id.getValue(), phone)) {
            throw new DuplicateException("Số điện thoại đã tồn tại");
        }
    }

}
