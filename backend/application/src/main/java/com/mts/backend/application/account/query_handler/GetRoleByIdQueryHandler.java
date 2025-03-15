package com.mts.backend.application.account.query_handler;

import com.mts.backend.application.account.query.RoleByIdQuery;
import com.mts.backend.application.account.response.RoleDetailResponse;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.repository.IRoleRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetRoleByIdQueryHandler implements IQueryHandler<RoleByIdQuery, CommandResult> {
    private final IRoleRepository roleRepository;

    public GetRoleByIdQueryHandler(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(RoleByIdQuery query) {
        Objects.requireNonNull(query, "RoleByIdQuery must not be null");
        
        var role = roleRepository.findById(RoleId.of(query.getId()))
                .orElseThrow(() -> new NotFoundException("Không tìm thấy role"));
        
        RoleDetailResponse roleDetailResponse = RoleDetailResponse.builder()
                .id(role.getId().getValue())
                .name(role.getRoleName().getValue())
                .description(role.getDescription().orElse(""))
                .build();
        
        return CommandResult.success(roleDetailResponse);
    }

    
}
