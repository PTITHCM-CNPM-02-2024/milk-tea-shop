package com.mts.backend.application.staff.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
public abstract class UserDetailResponse {
    protected Long id;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String gender;
    protected String phone;
}
