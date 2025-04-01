package com.mts.backend.application.staff.handler;

import com.mts.backend.application.staff.command.CreateManagerCommand;
import com.mts.backend.domain.account.AccountEntity;
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
public class CreateManagerCommandHandler implements ICommandHandler<CreateManagerCommand, CommandResult> {
    private final JpaManagerRepository managerRepository;
    private final JpaAccountRepository accountRepository;
    
    public CreateManagerCommandHandler(JpaManagerRepository managerRepository, JpaAccountRepository accountRepository) {
        this.managerRepository = managerRepository;
        this.accountRepository = accountRepository;
    }
    
    @Override
    @Transactional
    public CommandResult handle(CreateManagerCommand command) {
        
        verifyUniqueEmail(command.getEmail());
        verifyUniquePhone(command.getPhone());
        
        var account = mustExistAccountAndUnique(command.getAccountId());

        ManagerEntity manager = ManagerEntity.builder()
                .id(ManagerId.create().getValue())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .email(command.getEmail())
                .phone(command.getPhone())
                .gender(command.getGender())
                .accountEntity(account)
                .build();
        
        var savedManager = managerRepository.save(manager);
        
        return  CommandResult.success(savedManager.getId());
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
    
    private AccountEntity mustExistAccountAndUnique(AccountId accountId){
        Objects.requireNonNull(accountId, "Account id is required");
        if (!accountRepository.existsById(accountId.getValue())){
            throw new NotFoundException("Tài khoản không tồn tại");
        }
        if (managerRepository.existsByAccountId(accountId.getValue())){
            throw new DuplicateException("Tài khoản đã được sử dụng");
        }
        
        
        return accountRepository.getReferenceById(accountId.getValue());
    }
}
