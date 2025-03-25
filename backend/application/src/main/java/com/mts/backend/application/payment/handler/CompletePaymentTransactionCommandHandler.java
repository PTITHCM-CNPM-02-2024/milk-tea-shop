package com.mts.backend.application.payment.handler;

import com.mts.backend.application.billing.command.CreateInvoiceCommand;
import com.mts.backend.application.payment.command.PaymentTransactionCommand;
import com.mts.backend.application.payment.provider.IPaymentProvider;
import com.mts.backend.application.payment.provider.IPaymentProviderFactory;
import com.mts.backend.application.payment.response.PaymentResult;
import com.mts.backend.domain.order.Order;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.repository.IOrderRepository;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.payment.Payment;
import com.mts.backend.domain.payment.identifier.PaymentId;
import com.mts.backend.domain.payment.repository.IPaymentRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;
@Service
public class CompletePaymentTransactionCommandHandler implements ICommandHandler<PaymentTransactionCommand, CommandResult> {
    private final IPaymentProviderFactory paymentProviderFactory;
    private final IPaymentRepository paymentRepository;
    private final IOrderRepository orderRepository;
    private final ICommandHandler<CreateInvoiceCommand, CommandResult> invoiceCommandHandler;
    
    public CompletePaymentTransactionCommandHandler(IPaymentProviderFactory paymentProviderFactory,
                                                    IPaymentRepository paymentRepository,
                                                    IOrderRepository orderRepository,
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
        Order order = mustExistOrder(payment.getOrderId());
        
        PaymentResult paymentResult = paymentProvider.dispatch(payment, order, command);
        
        order.changeStatus(OrderStatus.COMPLETED);
        
        orderRepository.save(order);
        
        var result = invoiceCommandHandler.handle(CreateInvoiceCommand.builder()
                .order(order)
                .payment(payment)
                .build());
        
        return CommandResult.success(result.getData());
        
    }
    @Transactional
    protected Payment mustExistPayment(PaymentId id) {
        return paymentRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy thanh toán"));
    }


    @Transactional
    protected Order mustExistOrder(OrderId id) {
        return orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng"));
    }
    
    
}
