package com.mts.backend.application.staff.handler;

import com.mts.backend.application.staff.command.UpdateManagerCommand;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.staff.ManagerEntity;
import com.mts.backend.domain.staff.identifier.ManagerId;
import com.mts.backend.domain.staff.jpa.JpaManagerRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
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
        
        ManagerEntity manager = mustExistManager(command.getId());
        
        if (manager.changeEmail(command.getEmail())) {
            verifyUniqueEmail(command.getId(), command.getEmail());
        }
        
        if (manager.changePhoneNumber(command.getPhone())) {
            verifyUniquePhoneNumber(command.getId(), command.getPhone());
        }
        
        manager.changeFirstName(command.getFirstName());
        
        manager.changeLastName(command.getLastName());
        
        manager.changeGender(command.getGender());
        
        
        return CommandResult.success(manager.getId());
    }

    private ManagerEntity mustExistManager(ManagerId id) {
        Objects.requireNonNull(id, "Manager id is required");

        return managerRepository.findById(id.getValue())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy quản lý"));
    }
    
    private void verifyUniqueEmail(ManagerId id, Email email) {
        Objects.requireNonNull(email, "Manager is required");
        if (managerRepository.existsByIdNotAndEmail(id.getValue(), email)) {
            throw new DuplicateException("Email đã tồn tại");
        }
    }
    
    private void verifyUniquePhoneNumber( ManagerId id,  PhoneNumber phone) {
        Objects.requireNonNull(phone, "Manager is required");
        if (managerRepository.existsByIdNotAndPhone(id.getValue(), phone)) {
            throw new DuplicateException("Số điện thoại đã tồn tại");
        }
    }

    private void mustExitsAccount(AccountId accountId) {
        Objects.requireNonNull(accountId, "Account id is required");
        if (!accountRepository.existsById(accountId.getValue())) {
            throw new DuplicateException("Account không tồn tại");
        }

    }
}
