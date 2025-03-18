package com.mts.backend.application.staff.response;

import lombok.Data;


@Data
public abstract class UserDetailResponse {
    protected Long id;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String gender;
    protected String phone;
}
