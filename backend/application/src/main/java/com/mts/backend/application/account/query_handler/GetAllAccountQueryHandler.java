package com.mts.backend.application.account.query_handler;

import com.mts.backend.application.account.query.DefaultAccountQuery;
import com.mts.backend.application.account.response.AccountDetailResponse;
import com.mts.backend.application.account.response.RoleDetailResponse;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.account.jpa.JpaRoleRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllAccountQueryHandler implements IQueryHandler<DefaultAccountQuery, CommandResult> {
    private final JpaAccountRepository accountRepository;
    private final JpaRoleRepository roleRepository;
    
    public GetAllAccountQueryHandler(JpaAccountRepository accountRepository, JpaRoleRepository roleRepository) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
    }
    
    @Override
    @Transactional
    public CommandResult handle(DefaultAccountQuery query) {
        Objects.requireNonNull(query, "DefaultAccountQuery must not be null");
        
        var accounts = accountRepository.findAll(Pageable.ofSize(query.getSize()).withPage(query.getPage()));

        List<AccountDetailResponse> accountDetailResponses = new ArrayList<>();
        
        accounts.forEach(account -> {
            
            accountDetailResponses.add(AccountDetailResponse.builder()
                    .id(account.getId())
                    .username(account.getUsername().getValue())
                    .role(RoleDetailResponse.builder()
                            .id(account.getRoleEntity().getId())
                            .name(account.getRoleEntity().getName().getValue())
                            .description(account.getRoleEntity().getDescription().orElse(null))
                            .build())
                    .build());
            
        });
        
        return CommandResult.success(accountDetailResponses);
    }
}
