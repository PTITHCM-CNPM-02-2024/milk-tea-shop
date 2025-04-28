package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.UpdateRoleCommand;
import com.mts.backend.domain.account.Role;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.jpa.JpaRoleRepository;
import com.mts.backend.domain.account.value_object.RoleName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
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

        try {
            Role role = mustExistRoleId(command.getId());

            role.setName(command.getRoleName());

            role.setDescription(command.getDescription().orElse(null));

            var updatedRole = roleRepository.saveAndFlush(role);
            return CommandResult.success(role.getId());

        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_role_name")) {
                throw new DuplicateException("Tên vai trò đã tồn tại");
            }
            throw e;
        }

    }

    private Role mustExistRoleId(RoleId roleId) {
        return roleRepository.findById(roleId.getValue())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy role"));
    }

}
