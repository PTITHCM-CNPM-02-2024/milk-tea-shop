package com.mts.backend.application.staff.handler;

import com.mts.backend.application.staff.command.DeleteEmpByIdCommand;
import com.mts.backend.domain.staff.jpa.JpaEmployeeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class DeleteEmployeeByIdCommandHandler implements ICommandHandler<DeleteEmpByIdCommand, CommandResult> {
    
    private final JpaEmployeeRepository employeeRepository;
    
    public DeleteEmployeeByIdCommandHandler(JpaEmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
    
    @Override
    @Transactional
    public CommandResult handle(DeleteEmpByIdCommand command){
        Objects.requireNonNull(command, "DeleteEmpByIdCommand must not be null");
        
        var emp = employeeRepository.findById(command.getId().getValue())
                .orElseThrow(() -> new IllegalArgumentException("Nhân viên không tồn tại"));
        
        employeeRepository.delete(emp);
        
        return CommandResult.success(emp.getId());
    }
}
