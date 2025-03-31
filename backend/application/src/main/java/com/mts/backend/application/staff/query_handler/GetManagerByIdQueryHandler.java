package com.mts.backend.application.staff.query_handler;

import com.mts.backend.application.staff.query.ManagerByIdQuery;
import com.mts.backend.application.staff.response.ManagerDetailResponse;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.staff.ManagerEntity;
import com.mts.backend.domain.staff.identifier.ManagerId;
import com.mts.backend.domain.staff.jpa.JpaManagerRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetManagerByIdQueryHandler implements IQueryHandler<ManagerByIdQuery, CommandResult> {
    private final JpaManagerRepository managerRepository;
    private final JpaAccountRepository accountRepository;
    
    public GetManagerByIdQueryHandler(JpaManagerRepository managerRepository, JpaAccountRepository accountRepository) {
        this.managerRepository = managerRepository;
        this.accountRepository = accountRepository;
    }
    
    @Override
    @Transactional
    public CommandResult handle(ManagerByIdQuery query) {
        Objects.requireNonNull(query, "Manager by id query is required");
        var manager = mustExistManager((query.getId()));
        
        var response = ManagerDetailResponse.builder().build();
        
        response.setId(manager.getId());
        response.setFirstName(manager.getFirstName().getValue());
        response.setLastName(manager.getLastName().getValue());
        response.setEmail(manager.getEmail().getValue());
        response.setPhone(manager.getPhone().getValue());
        response.setGender(manager.getGender().toString());
        response.setAccountId(manager.getAccountEntity().getId());
        
        return CommandResult.success(response);
    }
    
    private ManagerEntity mustExistManager(ManagerId id){
        Objects.requireNonNull(id, "Manager id is required");
        
        return managerRepository.findByIdWithJoinFetch(id)
                .orElseThrow(() -> new NotFoundException("Quản lý không tồn tại"));
    }
    
}
