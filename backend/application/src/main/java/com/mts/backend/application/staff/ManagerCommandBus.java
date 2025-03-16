package com.mts.backend.application.staff;

import com.mts.backend.application.account.AccountCommandBus;
import com.mts.backend.application.staff.command.CreateManagerCommand;
import com.mts.backend.application.staff.command.UpdateManagerCommand;
import com.mts.backend.application.staff.handler.CreateManagerCommandHandler;
import com.mts.backend.application.staff.handler.UpdateManagerCommandHandler;
import com.mts.backend.domain.staff.Manager;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class ManagerCommandBus extends AbstractCommandBus {
    public ManagerCommandBus (CreateManagerCommandHandler createManagerCommandHandler, UpdateManagerCommandHandler updateManagerCommandHandler) {
        register(CreateManagerCommand.class, createManagerCommandHandler);
        register(UpdateManagerCommand.class, updateManagerCommandHandler);
    }
}
