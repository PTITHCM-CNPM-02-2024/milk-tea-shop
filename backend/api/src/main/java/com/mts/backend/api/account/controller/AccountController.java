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
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<ApiResponse<Long>> createAccount(@RequestBody CreateAccountRequest request) {
        CreateAccountCommand command = CreateAccountCommand.builder()
            .username(request.getUsername())
            .password(request.getPassword())
            .roleId(request.getRoleId())
            .build();
        
        var result = accountCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long)result.getData(),"Tạo tài khoản thành công")) : handleError(result);
        
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Long>> updateAccount(@PathVariable("id") Long id, @RequestBody UpdateAccountRequest request) {
        UpdateAccountCommand command = UpdateAccountCommand.builder()
            .id(id)
            .username(request.getUsername())
            .build();
        
        var result = accountCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long)result.getData(),"Cập nhật tài khoản thành công")) : handleError(result);
        
    }
    
    @PutMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse<Long>> changePassword(@PathVariable("id") Long id, @RequestBody UpdateAccountRequest request) {
        UpdateAccountPasswordCommand command = UpdateAccountPasswordCommand.builder()
            .id(id)
            .newPassword(request.getNewPassword())
            .confirmPassword(request.getConfirmPassword())
            .build();
        
        var result = accountCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long)result.getData(),"Cập nhật mật khẩu thành công")) : handleError(result);
        
    }
    
    @PutMapping("/{id}/change-role")
    public ResponseEntity<ApiResponse<Long>> changeRole(@PathVariable("id") Long id, @RequestBody UpdateAccountRequest request) {
        UpdateAccountRoleCommand command = UpdateAccountRoleCommand.builder()
            .id(id)
            .roleId(request.getRoleId())
            .build();
        
        var result = accountCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long)result.getData(),"Cập nhật role thành công")) : handleError(result);
        
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountDetailResponse>> getAccount(@PathVariable("id") Long id) {
        AccountByIdQuery query = AccountByIdQuery.builder()
            .id(id)
            .build();
        
        var result = accountQueryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((AccountDetailResponse)result.getData())) : handleError(result);
        
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountDetailResponse>>> getAccounts() {
        DefaultAccountQuery query = DefaultAccountQuery.builder()
            .build();
        
        var result = accountQueryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((List<AccountDetailResponse>)result.getData())) : handleError(result);
        
    }
    
    
    
}
