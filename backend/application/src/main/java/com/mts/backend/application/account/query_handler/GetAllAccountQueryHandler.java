package com.mts.backend.application.account.query_handler;

import com.mts.backend.application.account.query.DefaultAccountQuery;
import com.mts.backend.application.account.response.AccountDetailResponse;
import com.mts.backend.application.account.response.RoleDetailResponse;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.account.jpa.JpaRoleRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

        Page<AccountDetailResponse> accountDetailResponses = accounts.map(account -> {
            return AccountDetailResponse.builder()
                    .id(account.getId())
                    .username(account.getUsername().getValue())
                    .role(RoleDetailResponse.builder()
                            .id(account.getRole().getId())
                            .name(account.getRole().getName().getValue())
                            .description(account.getRole().getDescription().orElse(null))
                            .build())
                    .isLocked(account.getLocked())
                    .isActive(account.getActive().orElse(null))
                    .build();
        });
        
        return CommandResult.success(accountDetailResponses);
    }
}
