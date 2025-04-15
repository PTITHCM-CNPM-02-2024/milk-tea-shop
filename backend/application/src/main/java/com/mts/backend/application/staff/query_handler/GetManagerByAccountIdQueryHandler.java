package com.mts.backend.application.staff.query_handler;

import org.springframework.stereotype.Service;

import com.mts.backend.application.staff.query.GetManagerByAccountIdQuery;
import com.mts.backend.application.staff.response.ManagerDetailResponse;
import com.mts.backend.domain.staff.jpa.JpaManagerRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;

@Service
public class GetManagerByAccountIdQueryHandler implements IQueryHandler<GetManagerByAccountIdQuery, CommandResult> {
    private final JpaManagerRepository jpaManagerRepository;

    public GetManagerByAccountIdQueryHandler(JpaManagerRepository jpaManagerRepository) {
        this.jpaManagerRepository = jpaManagerRepository;
    }

    @Override
    public CommandResult handle(GetManagerByAccountIdQuery query) {
        var manager = jpaManagerRepository.findByAccountEntity_Id(query.getAccountId().getValue()).orElseThrow(() -> new NotFoundException("Manager not found"));

        var response = ManagerDetailResponse.builder()
                .id(manager.getId())
                .firstName(manager.getFirstName().getValue())
                .lastName(manager.getLastName().getValue())
                .email(manager.getEmail().getValue())
                .phone(manager.getPhone().getValue())
                .accountId(manager.getAccountEntity().getId())
                .build();

        return CommandResult.success(response);
    }
    
    
}