package com.mts.backend.application.staff.handler;

import com.mts.backend.application.staff.command.CreateManagerCommand;
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

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CreateManagerCommandHandler implements ICommandHandler<CreateManagerCommand, CommandResult> {
    private final IManagerRepository managerRepository;
    private final IAccountRepository accountRepository;
    
    public CreateManagerCommandHandler(IManagerRepository managerRepository, IAccountRepository accountRepository) {
        this.managerRepository = managerRepository;
        this.accountRepository = accountRepository;
    }
    
    @Override
    public CommandResult handle(CreateManagerCommand command) {
        Manager manager = new Manager(
                ManagerId.create(),
                FirstName.of(command.getFirstName()),
                LastName.of(command.getLastName()),
                Email.of(command.getEmail()),
                PhoneNumber.of(command.getPhone()),
                Gender.valueOf(command.getGender()),
                AccountId.of(command.getAccountId()),
                LocalDateTime.now());
        
        verifyUniqueEmail(manager.getEmail());
        verifyUniquePhone(manager.getPhoneNumber());
        
        mustExistAccountAndUnique(manager.getAccountId());
        
        var savedManager = managerRepository.save(manager);
        
        return  CommandResult.success(savedManager.getId().getValue());
    }
    
    private void verifyUniqueEmail (Email email){
        Objects.requireNonNull(email, "Email is required");
        if (managerRepository.existsByEmail(email)){
            throw new DuplicateException("Email đã tồn tại");
        }
        
    }
    
    private void verifyUniquePhone (PhoneNumber phoneNumber){
        Objects.requireNonNull(phoneNumber, "Phone number is required");
        if (managerRepository.existsByPhone(phoneNumber)){
            throw new DuplicateException("Số điện thoại đã tồn tại");
        }
    }
    
    private void mustExistAccountAndUnique(AccountId accountId){
        Objects.requireNonNull(accountId, "Account id is required");
        if (!accountRepository.existsById(accountId)){
            throw new NotFoundException("Tài khoản không tồn tại");
        }
        if (managerRepository.existsByAccountId(accountId)){
            throw new DuplicateException("Tài khoản đã được sử dụng");
        }
    }
}
