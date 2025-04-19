package com.mts.backend.application.store;

import com.mts.backend.application.store.command.CreateAreaCommand;
import com.mts.backend.application.store.command.DeleteAreaByIdCommand;
import com.mts.backend.application.store.command.UpdateAreaCommand;
import com.mts.backend.application.store.handler.CreateAreaCommandHandler;
import com.mts.backend.application.store.handler.DeleteAreaByIdCommandHandler;
import com.mts.backend.application.store.handler.UpdateAreaCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;
@Component
public class AreaCommandBus extends AbstractCommandBus {
    
    public AreaCommandBus (CreateAreaCommandHandler createAreaCommandHandler,
                           UpdateAreaCommandHandler updateAreaCommandHandler,
                           DeleteAreaByIdCommandHandler deleteAreaByIdCommandHandler){
        register(CreateAreaCommand.class, createAreaCommandHandler);
        register(UpdateAreaCommand.class, updateAreaCommandHandler);
        register(DeleteAreaByIdCommand.class, deleteAreaByIdCommandHandler);
    }
}
