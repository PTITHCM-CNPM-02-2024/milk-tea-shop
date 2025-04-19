package com.mts.backend.application.staff.handler;

import com.mts.backend.application.staff.command.UpdateManagerCommand;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.staff.ManagerEntity;
import com.mts.backend.domain.staff.identifier.ManagerId;
import com.mts.backend.domain.staff.jpa.JpaManagerRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateManagerCommandHandler implements ICommandHandler<UpdateManagerCommand, CommandResult> {

    private final JpaManagerRepository managerRepository;
    private final JpaAccountRepository accountRepository;

    public UpdateManagerCommandHandler(JpaManagerRepository managerRepository, JpaAccountRepository accountRepository) {
        this.managerRepository = managerRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * @param command
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(UpdateManagerCommand command) {
        Objects.requireNonNull(command, "Update manager command is required");

        try {
            var manager = mustExistManager(command.getId());

            manager.setEmail(command.getEmail());
            manager.setPhone(command.getPhone());
            manager.setFirstName(command.getFirstName());
            manager.setLastName(command.getLastName());
            manager.setGender(command.getGender());

            return CommandResult.success(manager.getId());
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate entry") &&
                e.getMessage().contains("uk_manager_email")) {
                throw new NotFoundException("Email đã tồn tại");
            }
            if (e.getMessage().contains("Duplicate entry") &&
                e.getMessage().contains("uk_manager_phone")) {
                throw new NotFoundException("Số điện thoại đã tồn tại");
            }
            throw new NotFoundException("Đã có lỗi xảy ra khi cập nhật quản lý", e);
        }
    }

    private ManagerEntity mustExistManager(ManagerId id) {
        Objects.requireNonNull(id, "Manager id is required");

        return managerRepository.findById(id.getValue())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy quản lý"));
    }
}
