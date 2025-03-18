package com.mts.backend.application.store;

import com.mts.backend.application.store.command.CreateServiceTableCommand;
import com.mts.backend.application.store.command.UpdateServiceTableCommand;
import com.mts.backend.application.store.handler.CreateServiceTableCommandHandler;
import com.mts.backend.application.store.handler.UpdateServiceTableCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class ServiceTableCommandBus extends AbstractCommandBus {
    public ServiceTableCommandBus(CreateServiceTableCommandHandler createServiceTableCommandHandler,
                                  UpdateServiceTableCommandHandler updateServiceTableCommandHandler){
        register(CreateServiceTableCommand.class, createServiceTableCommandHandler);
        register(UpdateServiceTableCommand.class, updateServiceTableCommandHandler);
    }
}
