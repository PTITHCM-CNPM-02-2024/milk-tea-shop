package com.mts.backend.application.account.query_handler;

import com.mts.backend.application.account.query.DefaultAccountQuery;
import com.mts.backend.application.account.response.AccountDetailResponse;
import com.mts.backend.application.account.response.RoleDetailResponse;
import com.mts.backend.domain.account.repository.IAccountRepository;
import com.mts.backend.domain.account.repository.IRoleRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllAccountQueryHandler implements IQueryHandler<DefaultAccountQuery, CommandResult> {
    private final IAccountRepository accountRepository;
    private final IRoleRepository roleRepository;
    
    public GetAllAccountQueryHandler(IAccountRepository accountRepository, IRoleRepository roleRepository) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
    }
    
    @Override
    public CommandResult handle(DefaultAccountQuery query) {
        Objects.requireNonNull(query, "DefaultAccountQuery must not be null");
        
        var accounts = accountRepository.findAll();

        List<AccountDetailResponse> accountDetailResponses = new ArrayList<>();
        
        accounts.forEach(account -> {
            var role = roleRepository.findById(account.getRoleId()).orElseThrow( () -> new NotFoundException("Không tìm thấy role"));
            
            var roleResponse = role != null ? RoleDetailResponse.builder()
                    .id(role.getId().getValue())
                    .name(role.getRoleName().getValue())
                    .description(role.getDescription().orElse(""))
                    .build() : null;
            
            accountDetailResponses.add(AccountDetailResponse.builder()
                    .id(account.getId().getValue())
                    .username(account.getUsername().getValue())
                    .role(roleResponse)
                    .build());
            
        });
        
        return CommandResult.success(accountDetailResponses);
    }
}
