package com.mts.backend.application.staff;

import com.mts.backend.application.staff.command.CreateEmployeeCommand;
import com.mts.backend.application.staff.command.UpdateEmployeeCommand;
import com.mts.backend.application.staff.handler.CreateEmployeeCommandHandler;
import com.mts.backend.application.staff.handler.UpdateEmployeeCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class EmployeeCommandBus extends AbstractCommandBus {
    
    public EmployeeCommandBus(CreateEmployeeCommandHandler createEmployeeCommandHandler, UpdateEmployeeCommandHandler updateEmployeeCommandHandler) {
        register(CreateEmployeeCommand.class, createEmployeeCommandHandler);
        register(UpdateEmployeeCommand.class, updateEmployeeCommandHandler);
        
    }
}
