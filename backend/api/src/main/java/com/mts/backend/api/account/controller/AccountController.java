package com.mts.backend.api.account.controller;

import com.mts.backend.api.account.request.CreateAccountRequest;
import com.mts.backend.api.account.request.UpdateAccountRequest;
import com.mts.backend.api.common.IController;
import com.mts.backend.application.account.AccountCommandBus;
import com.mts.backend.application.account.AccountQueryBus;
import com.mts.backend.application.account.command.CreateAccountCommand;
import com.mts.backend.application.account.command.UpdateAccountCommand;
import com.mts.backend.application.account.command.UpdateAccountPasswordCommand;
import com.mts.backend.application.account.command.UpdateAccountRoleCommand;
import com.mts.backend.application.account.query.AccountByIdQuery;
import com.mts.backend.application.account.query.DefaultAccountQuery;
import com.mts.backend.application.account.response.AccountDetailResponse;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.account.value_object.Username;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController implements IController {
    private final AccountCommandBus accountCommandBus;
    private final AccountQueryBus accountQueryBus;
    
    public AccountController(AccountCommandBus accountCommandBus, AccountQueryBus accountQueryBus) {
        this.accountCommandBus = accountCommandBus;
        this.accountQueryBus = accountQueryBus;
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Long>> createAccount(@RequestBody CreateAccountRequest request) {
        CreateAccountCommand command = CreateAccountCommand.builder()
            .username(Username.builder().value(request.getUsername()).build())
            .password(PasswordHash.builder().value(request.getPassword()).build())
            .roleId(RoleId.of(request.getRoleId()))
            .build();
        
        var result = accountCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long)result.getData(),"Tạo tài khoản thành công")) : handleError(result);
        
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Long>> updateAccount(@PathVariable("id") Long id, @RequestBody UpdateAccountRequest request) {
        UpdateAccountCommand command = UpdateAccountCommand.builder()
            .id(AccountId.of(id))
            .username(Username.builder().value(request.getUsername()).build())
            .build();
        
        var result = accountCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long)result.getData(),"Cập nhật tài khoản thành công")) : handleError(result);
        
    }
    
    @PutMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse<Long>> changePassword(@PathVariable("id") Long id, @RequestBody UpdateAccountRequest request) {
        UpdateAccountPasswordCommand command = UpdateAccountPasswordCommand.builder()
            .id(AccountId.of(id))
            .newPassword(PasswordHash.builder().value(request.getNewPassword()).build())
            .confirmPassword(PasswordHash.builder().value(request.getConfirmPassword()).build())
            .build();
        
        var result = accountCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long)result.getData(),"Cập nhật mật khẩu thành công")) : handleError(result);
        
    }
    
    @PutMapping("/{id}/change-role")
    public ResponseEntity<ApiResponse<Long>> changeRole(@PathVariable("id") Long id, @RequestBody UpdateAccountRequest request) {
        UpdateAccountRoleCommand command = UpdateAccountRoleCommand.builder()
            .id(AccountId.of(id))
            .roleId(RoleId.of(request.getRoleId()))
            .build();
        
        var result = accountCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long)result.getData(),"Cập nhật role thành công")) : handleError(result);
        
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountDetailResponse>> getAccount(@PathVariable("id") Long id) {
        AccountByIdQuery query = AccountByIdQuery.builder()
            .id(AccountId.of(id))
            .build();
        
        var result = accountQueryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((AccountDetailResponse)result.getData())) : handleError(result);
        
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAccounts(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                                 @RequestParam(value = "size", defaultValue = "10") int size) {
        DefaultAccountQuery query = DefaultAccountQuery.builder()
            .page(page)
            .size(size)
            .build();
        
        var result = accountQueryBus.dispatch(query);
        
        return result.isSuccess() ?
                ResponseEntity.ok(ApiResponse.success((result.getData()))) : handleError(result);
        
    }
    
    
    
}
