package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.CreateRoleCommand;
import com.mts.backend.domain.account.RoleEntity;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.jpa.JpaRoleRepository;
import com.mts.backend.domain.account.value_object.RoleName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CreateRoleCommandHandler implements ICommandHandler<CreateRoleCommand, CommandResult> {
    private final JpaRoleRepository roleRepository;
    
    public CreateRoleCommandHandler(JpaRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    
    @Override
    @Transactional
    public CommandResult handle(CreateRoleCommand command) {
        Objects.requireNonNull(command, "CreateRoleCommand must not be null");
        
        
        verifyRoleName(command.getName());

        RoleEntity role = RoleEntity.builder()
                .id(RoleId.create().getValue())
                .name(command.getName())
                .description(command.getDescription().orElse(null))
                .build();
        
        var savedRole = roleRepository.save(role);
        
        return CommandResult.success(savedRole.getId());
    }
    
    private void verifyRoleName(RoleName roleName) {
        Objects.requireNonNull(roleName, "Role name is required");
        
        if (roleRepository.existsByName(roleName)) {
            throw new DuplicateException("Tên vai trò đã tồn tại");
        }
    }
}
