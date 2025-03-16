package com.mts.backend.application.staff.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public abstract class UserBaseCommand {
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String gender;
    protected String phone;
    protected Long accountId;
}
