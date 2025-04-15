package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.UpdateRoleCommand;
import com.mts.backend.domain.account.RoleEntity;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.jpa.JpaRoleRepository;
import com.mts.backend.domain.account.value_object.RoleName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateRoleCommandHandler implements ICommandHandler<UpdateRoleCommand, CommandResult> {
    private final JpaRoleRepository roleRepository;
    
    public UpdateRoleCommandHandler(JpaRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(UpdateRoleCommand command) {
        Objects.requireNonNull(command, "UpdateRoleCommand must not be null");
        
        RoleEntity role = mustExistRoleId(command.getId());
        
        if (role.changeRoleName(command.getRoleName())) {
            verifyUniqueRoleName(command.getId(), role.getName());
        }
        
        role.changeDescription(command.getDescription().orElse(null));
        
        
        return CommandResult.success(role.getId());
    }
    
    private RoleEntity mustExistRoleId(RoleId roleId) {
        return roleRepository.findById(roleId.getValue())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy role"));
    }
    
    private void verifyUniqueRoleName(RoleId id,  RoleName role) {
        if (roleRepository.existsByIdNotAndName(id.getValue() ,role)) {
            throw new NotFoundException("Tên role đã tồn tại");
        }
    }
}
