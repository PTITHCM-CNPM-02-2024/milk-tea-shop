package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.UpdateRoleCommand;
import com.mts.backend.domain.account.Role;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.repository.IRoleRepository;
import com.mts.backend.domain.account.value_object.RoleName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateRoleCommandHandler implements ICommandHandler<UpdateRoleCommand, CommandResult> {
    private final IRoleRepository roleRepository;
    
    public UpdateRoleCommandHandler(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    public CommandResult handle(UpdateRoleCommand command) {
        Objects.requireNonNull(command, "UpdateRoleCommand must not be null");
        
        Role role = mustExistRoleId(RoleId.of(command.getId()));
        
        if (role.changeRoleName(RoleName.of(command.getRoleName()))) {
            verifyUniqueRoleName(role);
        }
        
        var savedRole = roleRepository.save(role);
        
        return CommandResult.success(savedRole.getId().getValue());
    }
    
    private Role mustExistRoleId(RoleId roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy role"));
    }
    
    private void verifyUniqueRoleName(Role role) {
        if (roleRepository.existsByName(role.getRoleName())) {
            throw new NotFoundException("Tên role đã tồn tại");
        }
    }
}
