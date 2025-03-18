package com.mts.backend.application.store;

import com.mts.backend.application.store.command.CreateStoreCommand;
import com.mts.backend.application.store.command.UpdateStoreCommand;
import com.mts.backend.application.store.handler.CreateStoreCommandHandler;
import com.mts.backend.application.store.handler.UpdateStoreCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class StoreCommandBus extends AbstractCommandBus {
    
    public StoreCommandBus(CreateStoreCommandHandler createStoreCommandHandler,
                           UpdateStoreCommandHandler updateStoreCommandHandler){
        register(CreateStoreCommand.class, createStoreCommandHandler);
        register(UpdateStoreCommand.class, updateStoreCommandHandler);
    }
}
