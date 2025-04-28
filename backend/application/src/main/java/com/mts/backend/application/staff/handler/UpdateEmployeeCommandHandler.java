package com.mts.backend.application.staff.handler;

import com.mts.backend.application.staff.command.UpdateEmployeeCommand;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.staff.Employee;
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
        
        
        try{
            Employee employee = mustExistEmployee(command.getId());
            employee.setEmail(command.getEmail());
            employee.setPhone(command.getPhone());
            employee.setPosition(command.getPosition());
            employee.setFirstName(command.getFirstName());
            employee.setLastName(command.getLastName());
            employee.setGender(command.getGender());
            
            employeeRepository.saveAndFlush(employee);
            return CommandResult.success(employee.getId());
        }catch (Exception e) {
            if (e.getMessage().contains("Duplicate entry") &&
                e.getMessage().contains("uk_employee_email")) {
                throw new DuplicateException("Email đã tồn tại");
            }
            if (e.getMessage().contains("Duplicate entry") &&
                e.getMessage().contains("uk_employee_phone")) {
                throw new DuplicateException("Số điện thoại đã tồn tại");
            }
            throw new NotFoundException("Đã có lỗi xảy ra khi cập nhật nhân viên", e);
        }
        
        
    }
    
    private Employee mustExistEmployee(EmployeeId employeeId) {
        Objects.requireNonNull(employeeId, "Employee id is required");
        return employeeRepository.findById(employeeId.getValue())
                .orElseThrow(() -> new NotFoundException("Nhân viên không tồn tại"));
    }
    

}
