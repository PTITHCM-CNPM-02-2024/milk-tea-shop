package com.mts.backend.application.staff.command;

import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.account.value_object.RoleName;
import com.mts.backend.domain.account.value_object.Username;
import com.mts.backend.domain.common.value_object.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public  class UserBaseCommand {
    protected FirstName firstName;
    protected LastName lastName;
    protected Email email;
    protected Gender gender;
    protected PhoneNumber phone;
    protected AccountId accountId;
    
    private Username username;
    private PasswordHash password;
    private RoleId roleId;
}
