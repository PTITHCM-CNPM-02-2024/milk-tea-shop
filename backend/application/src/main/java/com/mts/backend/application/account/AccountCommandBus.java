package com.mts.backend.application.account;

import com.mts.backend.application.account.command.*;
import com.mts.backend.application.account.handler.*;
import com.mts.backend.application.account.response.LogoutCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
public class AccountCommandBus extends AbstractCommandBus {
    
    public AccountCommandBus(CreateAccountCommandHandler createAccountCommand,
                             UpdateAccountCommandHandler updateAccountCommandHandler,
                             UpdateAccountPasswordCommandHandler updateAccountPasswordCommandHandler,
                             UpdateAccountRoleCommandHandler updateAccountRoleCommandHandler,
                             LockAccountCommandHandler lockAccountCommandHandler){
        register(CreateAccountCommand.class, createAccountCommand);
        register(UpdateAccountPasswordCommand.class, updateAccountPasswordCommandHandler);
        register(UpdateAccountCommand.class, updateAccountCommandHandler);
        register(UpdateAccountRoleCommand.class, updateAccountRoleCommandHandler);
        register(UpdateLockAccountCommand.class, lockAccountCommandHandler);
        
    }
}
