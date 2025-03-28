package com.mts.backend.application.order;

import com.mts.backend.application.order.command.CreateOrderCommand;
import com.mts.backend.application.order.handler.CreateOrderCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class OrderCommandBus extends AbstractCommandBus {
    
    public OrderCommandBus (CreateOrderCommandHandler createOrderCommandHandler){
        register(CreateOrderCommand.class, createOrderCommandHandler);
    }
}
