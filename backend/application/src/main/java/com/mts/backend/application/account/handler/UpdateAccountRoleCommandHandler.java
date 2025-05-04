package com.mts.backend.application.account.handler;

import com.mts.backend.application.account.command.UpdateAccountRoleCommand;
import com.mts.backend.domain.account.Account;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.account.jpa.JpaRoleRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class UpdateAccountRoleCommandHandler implements ICommandHandler<UpdateAccountRoleCommand, CommandResult> {
    JpaAccountRepository accountRepository;
    JpaRoleRepository roleRepository;


    @Override
    @Transactional
    public CommandResult handle(UpdateAccountRoleCommand command) {
        Objects.requireNonNull(command, "UpdateAccountCommand must not be null");

        try {
            Account account = mustBeExist(command.getId());

            account.setRole(roleRepository.getReferenceById(command.getRoleId().getValue()));

            account.incrementTokenVersion();

            var updatedAccount = accountRepository.saveAndFlush(account);

            accountRepository.grantPermissionsByRole(updatedAccount.getId());

            return CommandResult.success(account.getId());

        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("fk_account_role_id")) {
                throw new NotFoundException("Vai trò không tồn tại");
            }
            throw e;
        }

    }

    private Account mustBeExist(AccountId accountId) {
        Objects.requireNonNull(accountId, "AccountId must not be null");
        return accountRepository.findById(accountId.getValue())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài " +
                                                         "khoản"));
    }

}
