package com.mts.backend.application.account.query_handler;

import com.mts.backend.application.account.query.DefaultRoleQuery;
import com.mts.backend.application.account.response.RoleDetailResponse;
import com.mts.backend.domain.account.jpa.JpaRoleRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class GetAllRoleQueryHandler implements IQueryHandler<DefaultRoleQuery, CommandResult> {
    
    private final JpaRoleRepository roleRepository;
    
    public GetAllRoleQueryHandler(JpaRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    
    @Override
    public CommandResult handle(DefaultRoleQuery query) {
        Objects.requireNonNull(query, "GetAllRoleQuery must not be null");
        
        var roles = roleRepository.findAll(Pageable.ofSize(query.getSize()).withPage(query.getPage()));
        
        Page<RoleDetailResponse> roleDetailResponses = roles.map(role -> {
            return RoleDetailResponse.builder()
                    .id(role.getId())
                    .name(role.getName().getValue())
                    .description(role.getDescription().orElse(null))
                    .build();
        });
        
        return CommandResult.success(roleDetailResponses);
    }
}
