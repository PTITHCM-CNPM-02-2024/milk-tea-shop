package com.mts.backend.api.common.request;

import lombok.Data;

@Data
public abstract class UserBaseRequest {
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String gender;
    protected String phone;
    protected String username;
    protected String password;
    protected Integer roleId;
    protected Long accountId;
}
