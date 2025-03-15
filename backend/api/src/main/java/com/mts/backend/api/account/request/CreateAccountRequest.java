package com.mts.backend.api.account.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {
    
    private String username;
    private String password;
    private Integer roleId;
}
