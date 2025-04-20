package com.mts.backend.application.order.handler;

import com.mts.backend.application.order.command.CancelledOrderCommand;
import com.mts.backend.domain.order.Order;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.jpa.JpaOrderRepository;
import com.mts.backend.domain.order.value_object.PaymentStatus;
import com.mts.backend.domain.payment.Payment;
import com.mts.backend.domain.payment.jpa.JpaPaymentRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CancelledOrderCommandHandler implements ICommandHandler<CancelledOrderCommand, CommandResult> {
    private final JpaOrderRepository orderRepository;
    private final JpaPaymentRepository paymentRepository;
    public CancelledOrderCommandHandler( JpaOrderRepository orderRepository,
                                        JpaPaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }
    
    /**
     * @param command 
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(CancelledOrderCommand command) {
        Objects.requireNonNull(command, "CancelledOrderCommand is required");
        
        var order = mustExistOrder(command.getId());
        
        order.cancel();
        
        var orderSaved = orderRepository.save(order);
        
        var payments = getAllPayment(OrderId.of(orderSaved.getId()));
        
        for (var payment : payments) {
            payment.cancel();
        }
        
        var paymentSaved = paymentRepository.saveAll(payments);
        return CommandResult.success(orderSaved.getId());
    }
        
    
    private Order mustExistOrder(OrderId orderId) {
        Objects.requireNonNull(orderId, "Order id is required");
        return orderRepository.findById(orderId.getValue())
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }
    
    private List<Payment> getAllPayment(OrderId id){
        Objects.requireNonNull(id, "Order id is required");
        
        var payments =
                paymentRepository.findByOrderEntity_Id(id.getValue()).stream().filter(p -> p.getStatus().isEmpty() || p.getStatus().isPresent() && p.getStatus().get() != PaymentStatus.CANCELLED)
                        .toList();
        
        return payments;
    }
}
