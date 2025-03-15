package com.mts.backend.application.account;

import com.mts.backend.application.account.command.CreateRoleCommand;
import com.mts.backend.application.account.command.UpdateRoleCommand;
import com.mts.backend.application.account.handler.CreateRoleCommandHandler;
import com.mts.backend.application.account.handler.UpdateRoleCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class RoleCommandBus extends AbstractCommandBus {
    
    public RoleCommandBus(CreateRoleCommandHandler createRoleCommandHandler, UpdateRoleCommandHandler updateRoleCommandHandler) {
        register(CreateRoleCommand.class, createRoleCommandHandler);
        register(UpdateRoleCommand.class, updateRoleCommandHandler);
    }
}
