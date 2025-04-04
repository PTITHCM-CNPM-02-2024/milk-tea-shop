package com.mts.backend.application.order;

import com.mts.backend.application.order.command.CalculateOrderCommand;
import com.mts.backend.application.order.command.CancelledOrderCommand;
import com.mts.backend.application.order.command.CheckOutOrderCommand;
import com.mts.backend.application.order.command.CreateOrderCommand;
import com.mts.backend.application.order.handler.CalculateOrderCommandHandler;
import com.mts.backend.application.order.handler.CancelledOrderCommandHandler;
import com.mts.backend.application.order.handler.CheckOutOrderCommandHandler;
import com.mts.backend.application.order.handler.CreateOrderCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class OrderCommandBus extends AbstractCommandBus {
    
    public OrderCommandBus (CreateOrderCommandHandler createOrderCommandHandler,
                            CancelledOrderCommandHandler cancelledOrderCommandHandler,
                            CheckOutOrderCommandHandler checkOutOrderCommandHandler,
                            CalculateOrderCommandHandler orderProductCommandHandler) {
        register(CreateOrderCommand.class, createOrderCommandHandler);
        register(CancelledOrderCommand.class, cancelledOrderCommandHandler);
        register(CheckOutOrderCommand.class, checkOutOrderCommandHandler);
        register(CalculateOrderCommand.class, orderProductCommandHandler);
        
    }
}
