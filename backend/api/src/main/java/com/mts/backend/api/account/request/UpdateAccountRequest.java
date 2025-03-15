package com.mts.backend.api.account.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateAccountRequest {
    private String username;
    private Integer roleId;
    private String newPassword;
    private String confirmPassword;
}
