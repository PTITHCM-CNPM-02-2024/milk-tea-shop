package com.mts.backend.application.account.query_handler;

import com.mts.backend.application.account.query.AccountByIdQuery;
import com.mts.backend.application.account.response.AccountDetailResponse;
import com.mts.backend.application.account.response.RoleDetailResponse;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.account.jpa.JpaRoleRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetAccountByIdQueryHandler implements IQueryHandler<AccountByIdQuery, CommandResult> {
    private final JpaAccountRepository accountRepository;
    private final JpaRoleRepository roleRepository;

    public GetAccountByIdQueryHandler(JpaAccountRepository accountRepository, JpaRoleRepository roleRepository) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public CommandResult handle(AccountByIdQuery query) {
        Objects.requireNonNull(query, "AccountByIdQuery must not be null");

        var account = accountRepository.findById(query.getId().getValue())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));

        AccountDetailResponse accountDetailResponse = AccountDetailResponse.builder()
                .id(account.getId())
                .username(account.getUsername().getValue())
                .role(RoleDetailResponse.builder()
                        .id(account.getRoleEntity().getId())
                        .name(account.getRoleEntity().getName().getValue())
                        .description(account.getRoleEntity().getDescription().orElse(null))
                        .build())
                .build();

        return CommandResult.success(accountDetailResponse);
    }
}
