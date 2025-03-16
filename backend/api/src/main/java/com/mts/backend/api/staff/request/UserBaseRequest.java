package com.mts.backend.api.staff.request;

import lombok.Data;

@Data
public abstract class UserBaseRequest {
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String gender;
    protected String phone;
    protected Long accountId;
}
