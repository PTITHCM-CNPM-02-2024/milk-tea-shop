package com.mts.backend.application.account.query_handler;

import com.mts.backend.application.account.query.AccountByIdQuery;
import com.mts.backend.application.account.response.AccountDetailResponse;
import com.mts.backend.application.account.response.RoleDetailResponse;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.repository.IAccountRepository;
import com.mts.backend.domain.account.repository.IRoleRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetAccountByIdQueryHandler implements IQueryHandler<AccountByIdQuery , CommandResult> {
    private final IAccountRepository accountRepository;
    private final IRoleRepository roleRepository;
    
    public GetAccountByIdQueryHandler(IAccountRepository accountRepository, IRoleRepository roleRepository) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
    }
    
    @Override
    public CommandResult handle(AccountByIdQuery query) {
        Objects.requireNonNull(query, "AccountByIdQuery must not be null");
        
        var account = accountRepository.findById(AccountId.of(query.getId()))
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));
        
        var role = roleRepository.findById(account.getRoleId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy role"));

        AccountDetailResponse accountDetailResponse = AccountDetailResponse.builder()
                .id(account.getId().getValue())
                .username(account.getUsername().getValue())
                .role(RoleDetailResponse.builder()
                        .id(role.getId().getValue())
                        .name(role.getRoleName().getValue())
                        .description(role.getDescription().orElse(""))
                        .build())
                .build();
        
        return CommandResult.success(accountDetailResponse);
    }
}
