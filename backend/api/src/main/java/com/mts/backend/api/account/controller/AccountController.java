package com.mts.backend.api.account.controller;

import com.mts.backend.api.account.request.CreateAccountRequest;
import com.mts.backend.api.account.request.UpdateAccountRequest;
import com.mts.backend.api.common.IController;
import com.mts.backend.application.account.AccountCommandBus;
import com.mts.backend.application.account.AccountQueryBus;
import com.mts.backend.application.account.command.*;
import com.mts.backend.application.account.query.AccountByIdQuery;
import com.mts.backend.application.account.query.DefaultAccountQuery;
import com.mts.backend.application.security.model.UserPrincipal;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.account.value_object.Username;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController implements IController {
    private final AccountCommandBus accountCommandBus;
    private final AccountQueryBus accountQueryBus;

    public AccountController(AccountCommandBus accountCommandBus, AccountQueryBus accountQueryBus) {
        this.accountCommandBus = accountCommandBus;
        this.accountQueryBus = accountQueryBus;
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequest request) {
        CreateAccountCommand command = CreateAccountCommand.builder()
                .username(Username.builder().value(request.getUsername()).build())
                .password(PasswordHash.builder().value(request.getPassword()).build())
                .roleId(RoleId.of(request.getRoleId()))
                .build();

        var result = accountCommandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);

    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateAccount(@PathVariable("id") Long id,
                                           @RequestBody UpdateAccountRequest request,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails instanceof UserPrincipal userPrincipal && userPrincipal.getId() != null) {
            if (!Objects.equals(userPrincipal.getId(), AccountId.of(id))) {
                throw new AccessDeniedException("Bạn không có quyền truy cập vào tài khoản này");
            }
        }

        UpdateAccountCommand command = UpdateAccountCommand.builder()
                .id(AccountId.of(id))
                .username(Username.builder().value(request.getUsername()).build())
                .build();

        var result = accountCommandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);

    }

    @PutMapping("/{id}/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(@PathVariable("id") Long id,
                                            @RequestParam(value = "oldPassword", required = true) String oldPassword,
                                            @RequestParam(value = "newPassword", required = true) String newPassword,
                                            @RequestParam(value = "confirmPassword", required = true) String confirmPassword,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails instanceof UserPrincipal userPrincipal && userPrincipal.getId() != null) {
            if (!Objects.equals(userPrincipal.getId(), AccountId.of(id))) {
                throw new AccessDeniedException("Bạn không có quyền truy cập vào tài khoản này");
            }
        }
        UpdateAccountPasswordCommand command = UpdateAccountPasswordCommand.builder()
                .id(AccountId.of(id))
                .oldPassword(PasswordHash.builder().value(oldPassword).build())
                .newPassword(PasswordHash.builder().value(newPassword).build())
                .confirmPassword(PasswordHash.builder().value(confirmPassword).build())
                .build();

        var result = accountCommandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);

    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> changeRole(@PathVariable("id") Long id, @RequestParam("value") Integer roleId) {
        UpdateAccountRoleCommand command = UpdateAccountRoleCommand.builder()
                .id(AccountId.of(id))
                .roleId(RoleId.of(roleId))
                .build();

        var result = accountCommandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);

    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAccount(@PathVariable("id") Long id) {
        AccountByIdQuery query = AccountByIdQuery.builder()
                .id(AccountId.of(id))
                .build();

        var result = accountQueryBus.dispatch(query);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);

    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getAccounts(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size) {
        DefaultAccountQuery query = DefaultAccountQuery.builder()
                .page(page)
                .size(size)
                .build();

        var result = accountQueryBus.dispatch(query);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);

    }

    @PutMapping("/{id}/lock")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> lockAccount(@PathVariable("id") Long id, @RequestParam(value = "value",
            required = true) Boolean locked) {
        var command = UpdateLockAccountCommand.builder().id(AccountId.of(id))
                .isLocked(locked)
                .build();

        var result = accountCommandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
