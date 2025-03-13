package com.mts.backend.application.product;

import com.mts.backend.application.product.command.CreateUnitCommand;
import com.mts.backend.application.product.command.UpdateUnitCommand;
import com.mts.backend.application.product.handler.CreateUnitCommandHandler;
import com.mts.backend.application.product.handler.UpdateUnitCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class UnitCommandBus extends AbstractCommandBus {
    
    public UnitCommandBus(CreateUnitCommandHandler createUnitCommandHandler, UpdateUnitCommandHandler updateUnitCommandHandler) {
        register(CreateUnitCommand.class, createUnitCommandHandler);
        register(UpdateUnitCommand.class, updateUnitCommandHandler);
        
    }
}
