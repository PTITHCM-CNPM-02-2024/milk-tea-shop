package com.mts.backend.application.staff.handler;

import com.mts.backend.application.staff.command.UpdateManagerCommand;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.repository.IAccountRepository;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.staff.Manager;
import com.mts.backend.domain.staff.identifier.ManagerId;
import com.mts.backend.domain.staff.repository.IManagerRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateManagerCommandHandler implements ICommandHandler<UpdateManagerCommand, CommandResult> {

    private final IManagerRepository managerRepository;
    private final IAccountRepository accountRepository;

    public UpdateManagerCommandHandler(IManagerRepository managerRepository, IAccountRepository accountRepository) {
        this.managerRepository = managerRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * @param command
     * @return
     */
    @Override
    public CommandResult handle(UpdateManagerCommand command) {
        Objects.requireNonNull(command, "Update manager command is required");
        
        Manager manager = mustExistManager(ManagerId.of(command.getId()));
        
        if (manager.changeEmail(Email.of(command.getEmail()))) {
            verifyUniqueEmail(manager);
        }
        
        if (manager.changePhoneNumber(PhoneNumber.of(command.getPhone()))) {
            verifyUniquePhoneNumber(manager);
        }
        
        mustExitsAccount(manager.getAccountId());
        
        manager.changeFirstName(FirstName.of(command.getFirstName()));
        
        manager.changeLastName(LastName.of(command.getLastName()));
        
        manager.changeGender(Gender.valueOf(command.getGender()));
        
        var updatedManager = managerRepository.save(manager);
        
        return CommandResult.success(updatedManager.getId().getValue());
    }

    private Manager mustExistManager(ManagerId id) {
        Objects.requireNonNull(id, "Manager id is required");

        return managerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy quản lý"));
    }
    
    private void verifyUniqueEmail(Manager manager) {
        Objects.requireNonNull(manager, "Manager is required");
        if (managerRepository.existsByEmail(manager.getEmail())) {
            throw new DuplicateException("Email đã tồn tại");
        }
    }
    
    private void verifyUniquePhoneNumber(Manager manager) {
        Objects.requireNonNull(manager, "Manager is required");
        if (managerRepository.existsByPhone(manager.getPhoneNumber())) {
            throw new DuplicateException("Số điện thoại đã tồn tại");
        }
    }

    private void mustExitsAccount(AccountId accountId) {
        Objects.requireNonNull(accountId, "Account id is required");
        if (!accountRepository.existsById(accountId)) {
            throw new DuplicateException("Account không tồn tại");
        }

    }
}
