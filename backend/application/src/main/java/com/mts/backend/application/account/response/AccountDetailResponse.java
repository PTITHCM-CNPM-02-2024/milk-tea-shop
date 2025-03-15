package com.mts.backend.application.account.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AccountDetailResponse {
    private Long id;
    private String username;
    private String description;
    private boolean isActive;
    private boolean isLocked;
    private RoleDetailResponse role;
}
