package com.mts.backend.application.staff.query_handler;

import com.mts.backend.application.staff.query.DefaultManagerQuery;
import com.mts.backend.application.staff.response.ManagerDetailResponse;
import com.mts.backend.domain.staff.repository.IManagerRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllManagerQueryHandler implements IQueryHandler<DefaultManagerQuery, CommandResult> {
    private final IManagerRepository managerRepository;
    
    public GetAllManagerQueryHandler(IManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }
    
    @Override
    public CommandResult handle(DefaultManagerQuery query) {
        Objects.requireNonNull(query);
        
        var managers = managerRepository.findAll();
        List<ManagerDetailResponse> responses = new ArrayList<>();
        
        managers.forEach(manager -> {
            var response = ManagerDetailResponse.builder().build();
            response.setId(manager.getId().getValue());
            response.setFirstName(manager.getFirstName().getValue());
            response.setLastName(manager.getLastName().getValue());
            response.setEmail(manager.getEmail().getValue());
            response.setPhone(manager.getPhoneNumber().getValue());
            response.setGender(manager.getGender().toString());
            response.setAccountId(manager.getAccountId().getValue());
            responses.add(response);
        });
        
        return CommandResult.success(responses);
    }


}
