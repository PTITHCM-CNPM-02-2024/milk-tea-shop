package com.mts.backend.application.payment.handler;

import com.mts.backend.application.payment.command.CreatePaymentCommand;
import com.mts.backend.application.payment.provider.IPaymentProviderFactory;
import com.mts.backend.domain.order.Order;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.repository.IOrderRepository;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.payment.Payment;
import com.mts.backend.domain.payment.identifier.PaymentId;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.domain.payment.repository.IPaymentRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CreatePaymentCommandHandler implements ICommandHandler<CreatePaymentCommand, CommandResult> {
    
    private final IPaymentRepository paymentRepository;
    private final IOrderRepository orderRepository;
    private final IPaymentProviderFactory paymentProviderFactory;
    public CreatePaymentCommandHandler(IPaymentRepository paymentRepository, IOrderRepository orderRepository, IPaymentProviderFactory paymentProviderFactory) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paymentProviderFactory = paymentProviderFactory;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    public CommandResult handle(CreatePaymentCommand command) {
        Objects.requireNonNull(command, "CreatePaymentCommand is required");
        
        Order order = mustExistOrder(command.getOrderId());
        
        if (order.getFinalAmount().isEmpty()){
            throw new DomainException("Không thể tạo thanh toán cho đơn hàng không có giá trị");
        }
        
        Payment payment = new Payment(PaymentId.create(),
                order.getId(),
                command.getPaymentMethodId(),
                null, 
                null,
                null,
                Instant.now(),
                LocalDateTime.now());
        
        var paymentProvider = paymentProviderFactory.getPaymentProvider(payment.getPaymentMethodId());
        
        var paymentInitResponse = paymentProvider.initPayment(payment, order);
        
        return CommandResult.success(paymentInitResponse);
        
    }
    
    private Order mustExistOrder(OrderId id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng với id: " + id.getValue()));
    }
}
