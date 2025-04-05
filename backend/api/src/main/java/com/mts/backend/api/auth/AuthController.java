package com.mts.backend.api.auth;
import com.mts.backend.api.auth.request.AuthenticationRequest;
import com.mts.backend.application.account.AccountCommandBus;
import com.mts.backend.application.account.command.AuthenticationCommand;
import com.mts.backend.application.account.handler.LogoutCommand;
import com.mts.backend.application.security.model.UserPrincipal;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.account.value_object.Username;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Profile("prod")
public class AuthController {
    private final AccountCommandBus accountCommandBus;

    public AuthController(AccountCommandBus accountCommandBus) {
        this.accountCommandBus = accountCommandBus;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody AuthenticationRequest request) {
        var command = new AuthenticationCommand(
                Username.builder().value(request.getUsername()).build(),
                PasswordHash.builder().value(request.getPassword()).build()
        );
        
        var result = accountCommandBus.dispatch(command);
        
        return ResponseEntity.ok(ApiResponse.success(result.getData()));
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> logout(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        var command = LogoutCommand.builder()
                .userPrincipal(userPrincipal)
                .build();
        
        var result = accountCommandBus.dispatch(command);
        
        return ResponseEntity.ok(ApiResponse.success(result.getData()));
    }
}