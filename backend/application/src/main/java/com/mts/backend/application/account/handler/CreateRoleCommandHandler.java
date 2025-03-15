package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.CreateRoleCommand;
import com.mts.backend.domain.account.Role;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.repository.IRoleRepository;
import com.mts.backend.domain.account.value_object.RoleName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CreateRoleCommandHandler implements ICommandHandler<CreateRoleCommand, CommandResult> {
    private final IRoleRepository roleRepository;
    
    public CreateRoleCommandHandler(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    
    @Override
    public CommandResult handle(CreateRoleCommand command) {
        Objects.requireNonNull(command, "CreateRoleCommand must not be null");
        
        Role role = new Role(RoleId.create(), RoleName.of(command.getName()), command.getDescription(), LocalDateTime.now());
        
        verifyRoleName(command.getName());
        
        var savedRole = roleRepository.save(role);
        
        return CommandResult.success(savedRole.getId().getValue());
    }
    
    private void verifyRoleName(String roleName) {
        Objects.requireNonNull(roleName, "Role name is required");
        
        if (roleRepository.existsByName(RoleName.of(roleName))) {
            throw new DuplicateException("Role name already exists");
        }
    }
}
