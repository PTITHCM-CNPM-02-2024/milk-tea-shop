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
    public GetManagerByIdQueryHandler(JpaManagerRepository managerRepository, JpaAccountRepository accountRepository) {
        this.managerRepository = managerRepository;
    }
    
    @Override
    @Transactional
    public CommandResult handle(ManagerByIdQuery query) {
        Objects.requireNonNull(query, "Manager by id query is required");
        var manager = mustExistManager((query.getId()));
        
        var response = ManagerDetailResponse.builder()
                .id(manager.getId())
                .firstName(manager.getFirstName().getValue())
                .lastName(manager.getLastName().getValue())
                .email(manager.getEmail().getValue())
                .phone(manager.getPhone().getValue())
                .gender(manager.getGender().toString())
                .accountId(manager.getAccountEntity().getId())
                .build();
        
        return CommandResult.success(response);
    }
    
    private ManagerEntity mustExistManager(ManagerId id){
        Objects.requireNonNull(id, "Manager id is required");
        
        return managerRepository.findByIdWithJoinFetch(id.getValue())
                .orElseThrow(() -> new NotFoundException("Quản lý không tồn tại"));
    }
    
}
