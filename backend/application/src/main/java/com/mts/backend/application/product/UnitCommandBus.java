package com.mts.backend.application.product;

import com.mts.backend.application.product.command.CreateUnitCommand;
import com.mts.backend.application.product.handler.CreateUnitCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class UnitCommandBus extends AbstractCommandBus {
    
    public UnitCommandBus(CreateUnitCommandHandler createUnitCommandHandler) {
        register(CreateUnitCommand.class, createUnitCommandHandler);
    }
}
