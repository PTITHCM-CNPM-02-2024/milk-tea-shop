package com.mts.backend.application.order.handler;

import com.mts.backend.application.order.command.CheckOutOrderCommand;
import com.mts.backend.domain.order.OrderEntity;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.jpa.JpaOrderRepository;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CheckOutOrderCommandHandler implements ICommandHandler<CheckOutOrderCommand, CommandResult>
{
    private final JpaOrderRepository orderRepository;
    
    public CheckOutOrderCommandHandler(JpaOrderRepository orderRepository) {
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
        
        order.checkOut();
        
        return CommandResult.success(order.getId());
        
        
    }
    
    private OrderEntity mustExistOrder(OrderId orderId) {
        return orderRepository.findById(orderId.getValue())
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }
}
