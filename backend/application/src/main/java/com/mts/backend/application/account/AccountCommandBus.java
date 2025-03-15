package com.mts.backend.application.account;

import com.mts.backend.application.account.command.CreateAccountCommand;
import com.mts.backend.application.account.command.UpdateAccountCommand;
import com.mts.backend.application.account.command.UpdateAccountPasswordCommand;
import com.mts.backend.application.account.command.UpdateAccountRoleCommand;
import com.mts.backend.application.account.handler.CreateAccountCommandHandler;
import com.mts.backend.application.account.handler.UpdateAccountCommandHandler;
import com.mts.backend.application.account.handler.UpdateAccountPasswordCommandHandler;
import com.mts.backend.application.account.handler.UpdateAccountRoleCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class AccountCommandBus extends AbstractCommandBus {
    
    public AccountCommandBus(CreateAccountCommandHandler createAccountCommand, UpdateAccountCommandHandler updateAccountCommandHandler, UpdateAccountPasswordCommandHandler updateAccountPasswordCommandHandler, UpdateAccountRoleCommandHandler updateAccountRoleCommandHandler) {
        register(CreateAccountCommand.class, createAccountCommand);
        register(UpdateAccountPasswordCommand.class, updateAccountPasswordCommandHandler);
        register(UpdateAccountCommand.class, updateAccountCommandHandler);
        register(UpdateAccountRoleCommand.class, updateAccountRoleCommandHandler);
    }
}
