package com.mts.backend.application.account.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AccountDetailResponse {
    private Long id;
    private String username;
    private String description;
    private Boolean isActive;
    private Boolean isLocked;
    private RoleDetailResponse role;
    private Instant lastLogin;
}
