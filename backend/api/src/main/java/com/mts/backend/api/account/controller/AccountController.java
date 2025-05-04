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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Tag(name = "Account Controller", description = "Account")
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController implements IController {
    private final AccountCommandBus accountCommandBus;
    private final AccountQueryBus accountQueryBus;

    public AccountController(AccountCommandBus accountCommandBus, AccountQueryBus accountQueryBus) {
        this.accountCommandBus = accountCommandBus;
        this.accountQueryBus = accountQueryBus;
    }

    @Operation(summary = "Tạo tài khoản mới")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu đầu vào")
    })
    @PostMapping
    public ResponseEntity<?> createAccount(@Parameter(description = "Thông tin tài khoản", required = true) @RequestBody CreateAccountRequest request) {
        CreateAccountCommand command = CreateAccountCommand.builder()
                .username(Username.of(request.getUsername()))
                .password(PasswordHash.of(request.getPassword()))
                .roleId(RoleId.of(request.getRoleId()))
                .build();

        var result = accountCommandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);

    }

    @Operation(summary = "Cập nhật tài khoản")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản")
    })
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and authentication.principal.getId() == #id")
    public ResponseEntity<?> updateAccount(@Parameter(description = "ID tài khoản", required = true) @PathVariable("id") Long id,
                                           @Parameter(description = "Thông tin cập nhật", required = true) @RequestBody UpdateAccountRequest request) {

        UpdateAccountCommand command = UpdateAccountCommand.builder()
                .id(AccountId.of(id))
                .username(Username.of(request.getUsername()))
                .build();

        var result = accountCommandBus.dispatch(command);
        
        var refreshToken =accountCommandBus.dispatch(GenerateRefreshTokenCommand.builder().build());
        
        var cookie = ResponseCookie.fromClientResponse("refreshToken", (String)refreshToken.getData())
                .httpOnly(true)
                .path("/api/v1/auth/refresh")
                .sameSite("None")
                .secure(true)
                .build();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(result.getData());

    }

    @Operation(summary = "Đổi mật khẩu tài khoản")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @PutMapping("/{id}/password")
    @PreAuthorize("(isAuthenticated() and authentication.principal.getId() == #id) or hasAuthority('MANAGER')")
    public ResponseEntity<?> changePassword(@Parameter(description = "ID tài khoản", required = true) @PathVariable("id") Long id,
                                            @Parameter(description = "Mật khẩu cũ", required = true) @RequestParam(value = "oldPassword", required = true) String oldPassword,
                                            @Parameter(description = "Mật khẩu mới", required = true) @RequestParam(value = "newPassword", required = true) String newPassword,
                                            @Parameter(description = "Xác nhận mật khẩu", required = true) @RequestParam(value = "confirmPassword", required = true) String confirmPassword,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        UpdateAccountPasswordCommand command = UpdateAccountPasswordCommand.builder()
                .id(AccountId.of(id))
                .oldPassword(PasswordHash.of(oldPassword))
                .newPassword(PasswordHash.of(newPassword))
                .confirmPassword(PasswordHash.of(confirmPassword))
                .userDetails(userDetails)
                .build();

        var result = accountCommandBus.dispatch(command);
        
        var refreshToken = accountCommandBus.dispatch(GenerateRefreshTokenCommand.builder().build()).getData();
        
        var cookie = ResponseCookie.fromClientResponse("refreshToken", (String)refreshToken)
                .httpOnly(true)
                .path("/api/v1/auth/refresh")
                .sameSite("None")
                .secure(true)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(result.getData());

    }

    @Operation(summary = "Đổi vai trò tài khoản")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> changeRole(@Parameter(description = "ID tài khoản", required = true) @PathVariable("id") Long id, @Parameter(description = "ID vai trò", required = true) @RequestParam("value") Integer roleId) {
        UpdateAccountRoleCommand command = UpdateAccountRoleCommand.builder()
                .id(AccountId.of(id))
                .roleId(RoleId.of(roleId))
                .build();

        var result = accountCommandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);

    }

    @Operation(summary = "Lấy tài khoản theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAccount(@Parameter(description = "ID tài khoản", required = true) @PathVariable("id") Long id) {
        AccountByIdQuery query = AccountByIdQuery.builder()
                .id(AccountId.of(id))
                .build();

        var result = accountQueryBus.dispatch(query);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);

    }

    @Operation(summary = "Lấy danh sách tài khoản")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getAccounts(@Parameter(description = "Trang", required = false) @RequestParam(value = "page", defaultValue = "0") Integer page,
                                         @Parameter(description = "Kích thước trang", required = false) @RequestParam(value = "size", defaultValue = "10") Integer size) {
        DefaultAccountQuery query = DefaultAccountQuery.builder()
                .page(page)
                .size(size)
                .build();

        var result = accountQueryBus.dispatch(query);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);

    }

    @Operation(summary = "Khóa/mở khóa tài khoản")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @PutMapping("/{id}/lock")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> lockAccount(@Parameter(description = "ID tài khoản", required = true) @PathVariable("id") Long id, @Parameter(description = "Trạng thái khóa", required = true) @RequestParam(value = "value", required = true) Boolean locked) {
        var command = UpdateLockAccountCommand.builder().id(AccountId.of(id))
                .isLocked(locked)
                .build();

        var result = accountCommandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
