package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.CreateRoleCommand;
import com.mts.backend.domain.account.Role;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.jpa.JpaRoleRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
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

        try {
            Role role = Role.builder()
                    .id(RoleId.create())
                    .name(command.getName())
                    .description(command.getDescription().orElse(null))
                    .build();

            var savedRole = roleRepository.saveAndFlush(role);

            return CommandResult.success(savedRole.getId());
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_role_name")) {
                throw new DuplicateException("Tên vai trò đã tồn tại");
            }
            throw e;
        }
    }


}
