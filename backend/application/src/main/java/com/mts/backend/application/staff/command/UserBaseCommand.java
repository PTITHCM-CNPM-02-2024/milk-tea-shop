package com.mts.backend.application.staff.command;

import lombok.Data;

@Data
public abstract class UserBaseCommand {
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String gender;
    protected String phone;
    protected Long accountId;
}
