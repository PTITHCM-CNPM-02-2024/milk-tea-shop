package com.mts.backend.application.staff.query_handler;

import com.mts.backend.application.staff.query.DefaultManagerQuery;
import com.mts.backend.application.staff.response.ManagerDetailResponse;
import com.mts.backend.domain.staff.jpa.JpaManagerRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetAllManagerQueryHandler implements IQueryHandler<DefaultManagerQuery, CommandResult> {
    private final JpaManagerRepository managerRepository;
    
    public GetAllManagerQueryHandler(JpaManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }
    
    @Override
    public CommandResult handle(DefaultManagerQuery query) {
        Objects.requireNonNull(query);
        
        var managers = managerRepository.findAll(Pageable.ofSize(query.getSize()).withPage(query.getPage()));
        Page<ManagerDetailResponse> responses = managers.map(manager -> {
            return ManagerDetailResponse.builder()
                    .id(manager.getId())
                    .firstName(manager.getFirstName().getValue())
                    .lastName(manager.getLastName().getValue())
                    .email(manager.getEmail().getValue())
                    .phone(manager.getPhone().getValue())
                    .gender(manager.getGender().toString())
                    .accountId(manager.getAccount().getId())
                    .build();
        });
        
        return CommandResult.success(responses);
    }


}
