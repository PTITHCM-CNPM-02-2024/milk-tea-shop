package com.mts.backend.application.payment.handler;

import com.mts.backend.application.payment.command.CreatePaymentCommand;
import com.mts.backend.application.payment.provider.IPaymentProviderFactory;
import com.mts.backend.domain.order.Order;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.jpa.JpaOrderRepository;
import com.mts.backend.domain.payment.Payment;
import com.mts.backend.domain.payment.PaymentMethod;
import com.mts.backend.domain.payment.identifier.PaymentId;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.domain.payment.jpa.JpaPaymentMethodRepository;
import com.mts.backend.domain.payment.jpa.JpaPaymentRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Service
public class CreatePaymentCommandHandler implements ICommandHandler<CreatePaymentCommand, CommandResult> {
    
    private final JpaPaymentRepository paymentRepository;
    private final JpaOrderRepository orderRepository;
    private final JpaPaymentMethodRepository paymentMethodRepository;
    private final IPaymentProviderFactory paymentProviderFactory;
    public CreatePaymentCommandHandler(JpaPaymentRepository paymentRepository,
                                    JpaOrderRepository orderRepository,
                                    JpaPaymentMethodRepository paymentMethodRepository, 
                                    IPaymentProviderFactory paymentProviderFactory ) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentProviderFactory = paymentProviderFactory;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(CreatePaymentCommand command) {
        Objects.requireNonNull(command, "CreatePaymentCommand is required");
        
        Order order = mustExistOrder(command.getOrderId());
        
        PaymentMethod paymentMethod = mustExistPaymentMethod(command.getPaymentMethodId());
        
        if (order.getFinalAmount().isEmpty()){
            throw new DomainException("Không thể tạo thanh toán cho đơn hàng không có giá trị");
        }
        
        Payment payment = Payment.builder()
                .id(PaymentId.create().getValue())
                .orderEntity(order)
                .paymentMethod(paymentMethod)
                .paymentTime(Instant.now())
                .build();
        
        var paymentProvider = paymentProviderFactory.getPaymentProvider(PaymentMethodId.of(paymentMethod.getId()));
        
        var paymentInitResponse = paymentProvider.initPayment(payment, order);
        
        return CommandResult.success(paymentInitResponse);
        
    }
    
    private Order mustExistOrder(OrderId id) {
        return orderRepository.findById(id.getValue())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng với id: " + id.getValue()));
    }
    
    private PaymentMethod mustExistPaymentMethod(PaymentMethodId id) {
        return paymentMethodRepository.findById(id.getValue())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phương thức thanh toán với id: " + id.getValue()));
    }
}
