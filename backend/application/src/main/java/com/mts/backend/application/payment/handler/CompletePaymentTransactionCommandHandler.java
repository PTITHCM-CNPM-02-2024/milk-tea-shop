package com.mts.backend.application.payment.handler;

import com.mts.backend.application.billing.command.CreateInvoiceCommand;
import com.mts.backend.application.payment.command.PaymentTransactionCommand;
import com.mts.backend.application.payment.provider.IPaymentProvider;
import com.mts.backend.application.payment.provider.IPaymentProviderFactory;
import com.mts.backend.application.payment.response.PaymentResult;
import com.mts.backend.domain.order.Order;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.jpa.JpaOrderRepository;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.payment.Payment;
import com.mts.backend.domain.payment.identifier.PaymentId;
import com.mts.backend.domain.payment.jpa.JpaPaymentRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;
@Service
public class CompletePaymentTransactionCommandHandler implements ICommandHandler<PaymentTransactionCommand, CommandResult> {
    private final IPaymentProviderFactory paymentProviderFactory;
    private final JpaPaymentRepository paymentRepository;
    private final JpaOrderRepository orderRepository;
    private final ICommandHandler<CreateInvoiceCommand, CommandResult> invoiceCommandHandler;
    
    public CompletePaymentTransactionCommandHandler(IPaymentProviderFactory paymentProviderFactory,
                                                    JpaPaymentRepository paymentRepository,
                                                    JpaOrderRepository orderRepository,
                                                    ICommandHandler<CreateInvoiceCommand, CommandResult> invoiceCommandHandler) {
        this.paymentProviderFactory = paymentProviderFactory;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.invoiceCommandHandler = invoiceCommandHandler;
    }
    
    /**
     * @param command 
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(PaymentTransactionCommand command) {

        Objects.requireNonNull(command, "PaymentTransactionCommand is required");
        IPaymentProvider paymentProvider = paymentProviderFactory.getPaymentProvider(command.getPaymentMethodId());
        
        Payment payment = mustExistPayment(command.getPaymentId());
        Order order = mustExistOrder(OrderId.of(payment.getOrder().getId()));
        
        PaymentResult paymentResult = paymentProvider.dispatch(payment, order, command);
        
        order.setStatus(OrderStatus.COMPLETED);
        
        var result = invoiceCommandHandler.handle(CreateInvoiceCommand.builder()
                .order(order)
                .payment(payment)
                .build());
        
        return CommandResult.success(result.getData());
        
    }
    private Payment mustExistPayment(PaymentId id) {
        return paymentRepository.findById(id.getValue()).orElseThrow(() -> new NotFoundException("Không tìm thấy thanh toán"));
    }


    private Order mustExistOrder(OrderId id) {
        return orderRepository.findOrderWithDetails(id.getValue()).orElseThrow(() -> new NotFoundException("Không tìm thấy " +
                "đơn hàng"));
    }
    
    
}
