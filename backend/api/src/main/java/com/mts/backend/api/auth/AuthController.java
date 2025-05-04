package com.mts.backend.api.auth;
import com.mts.backend.api.auth.request.AuthenticationRequest;
import com.mts.backend.application.account.AuthCommandBus;
import com.mts.backend.application.account.command.AuthenticationCommand;
import com.mts.backend.application.account.command.GenerateRefreshTokenCommand;
import com.mts.backend.application.account.command.LogoutCommand;
import com.mts.backend.application.account.command.RefreshAccessTokenCommand;
import com.mts.backend.application.account.response.AuthenticationResponse;
import com.mts.backend.application.security.model.UserPrincipal;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.account.value_object.Username;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
        
        var refreshToken = accountCommandBus.dispatch(GenerateRefreshTokenCommand.builder().build()).getData();
        
        
        var cookie = ResponseCookie.fromClientResponse("refreshToken", refreshToken.toString())
                .httpOnly(true)
                .path("/api/v1/auth/refresh")
                .build();
        
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(result.getData());
        
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
    
    @Operation(summary = "Làm mới access token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Làm mới access token thành công"),
        @ApiResponse(responseCode = "401", description = "Token không hợp lệ")
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Parameter(description = "Cookie chứa refresh token", required = true) @CookieValue(value = "refreshToken", defaultValue = "") String refreshToken)  {
        var command = RefreshAccessTokenCommand.builder()
                .refreshToken(refreshToken)
                .build();
        
        var result = accountCommandBus.dispatch(command);
        
        var refresh = accountCommandBus.dispatch(GenerateRefreshTokenCommand.builder().build()).getData();
        
        var cookie = ResponseCookie.fromClientResponse("refreshToken", refresh.toString())
                .httpOnly(true)
                .path("/api/v1/auth/refresh")
                .secure(false)
                .build();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(result.getData());
        
    }
}