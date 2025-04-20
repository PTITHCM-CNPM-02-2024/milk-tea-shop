package com.mts.backend.api.auth;
import com.mts.backend.api.auth.request.AuthenticationRequest;
import com.mts.backend.application.account.AuthCommandBus;
import com.mts.backend.application.account.command.AuthenticationCommand;
import com.mts.backend.application.account.handler.LogoutCommand;
import com.mts.backend.application.security.model.UserPrincipal;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.account.value_object.Username;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "Auth Controller", description = "Auth")
@RestController
@RequestMapping("/api/v1/auth")
@Profile("dev")
public class AuthController {
    private final AuthCommandBus accountCommandBus;

    public AuthController(AuthCommandBus accountCommandBus) {
        this.accountCommandBus = accountCommandBus;
    }

    @Operation(summary = "Đăng nhập")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Đăng nhập thành công"),
        @ApiResponse(responseCode = "401", description = "Sai thông tin đăng nhập")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Parameter(description = "Thông tin đăng nhập", required = true) @RequestBody AuthenticationRequest request) {
        var command = new AuthenticationCommand(
                Username.of(request.getUsername()),
                PasswordHash.of(request.getPassword())
        );
        
        var result = accountCommandBus.dispatch(command);
        
        return ResponseEntity.ok(result.getData());
    }

    @Operation(summary = "Đăng xuất")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Đăng xuất thành công")
    })
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logout(@Parameter(description = "Thông tin người dùng", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        var command = LogoutCommand.builder()
                .userPrincipal(userPrincipal)
                .build();
        
        var result = accountCommandBus.dispatch(command);
        
        return ResponseEntity.ok(result.getData());
    }
}