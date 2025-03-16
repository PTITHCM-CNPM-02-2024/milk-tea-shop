package com.mts.backend.application.staff.query_handler;

import com.mts.backend.application.staff.query.ManagerByIdQuery;
import com.mts.backend.application.staff.response.ManagerDetailResponse;
import com.mts.backend.domain.account.Account;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.repository.IAccountRepository;
import com.mts.backend.domain.staff.Manager;
import com.mts.backend.domain.staff.identifier.ManagerId;
import com.mts.backend.domain.staff.repository.IManagerRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetManagerByIdQueryHandler implements IQueryHandler<ManagerByIdQuery, CommandResult> {
    private final IManagerRepository managerRepository;
    private final IAccountRepository accountRepository;
    
    public GetManagerByIdQueryHandler(IManagerRepository managerRepository, IAccountRepository accountRepository) {
        this.managerRepository = managerRepository;
        this.accountRepository = accountRepository;
    }
    
    @Override
    public CommandResult handle(ManagerByIdQuery query) {
        Objects.requireNonNull(query, "Manager by id query is required");
        var manager = mustExistManager(ManagerId.of(query.getId()));
        
        var account = mustExistAccount(manager.getAccountId());
        
        var response = ManagerDetailResponse.builder().build();
        
        response.setId(manager.getId().getValue());
        response.setFirstName(manager.getFirstName().getValue());
        response.setLastName(manager.getLastName().getValue());
        response.setEmail(manager.getEmail().getValue());
        response.setPhone(manager.getPhoneNumber().getValue());
        response.setGender(manager.getGender().toString());
        response.setAccountId(account.getId().getValue());
        
        return CommandResult.success(response);
    }
    
    private Manager mustExistManager(ManagerId id){
        Objects.requireNonNull(id, "Manager id is required");
        
        return managerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Quản lý không tồn tại"));
    }
    
    private Account mustExistAccount(AccountId accountId){
        Objects.requireNonNull(accountId, "Account id is required");
        
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Tài khoản không tồn tại"));
    }
}
