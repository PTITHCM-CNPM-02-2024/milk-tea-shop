package com.mts.backend.application.order.handler;

import com.mts.backend.application.order.command.CheckOutOrderCommand;
import com.mts.backend.domain.order.Order;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.repository.IOrderRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CheckOutOrderCommandHandler implements ICommandHandler<CheckOutOrderCommand, CommandResult>
{
    private final IOrderRepository orderRepository;
    
    public CheckOutOrderCommandHandler(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    /**
     * @param command 
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(CheckOutOrderCommand command) {
        Objects.requireNonNull(command, "CheckOutOrderCommand is required");
        
        var order = mustExistOrder(command.getOrderId());
        
        order.checkOutAllTable();
        
        var orderSaved = orderRepository.save(order);
        
        return CommandResult.success(orderSaved.getId().getValue());
        
        
    }
    
    @Transactional
    protected Order mustExistOrder(OrderId orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }
}
