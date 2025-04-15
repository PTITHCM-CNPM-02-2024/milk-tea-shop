package com.mts.backend.application.payment;

import com.mts.backend.application.payment.command.CreatePaymentCommand;
import com.mts.backend.application.payment.command.CreatePaymentMethodCommand;
import com.mts.backend.application.payment.command.DeletePmtByIdCommand;
import com.mts.backend.application.payment.command.PaymentTransactionCommand;
import com.mts.backend.application.payment.command.UpdatePaymentMethodCommand;
import com.mts.backend.application.payment.handler.CompletePaymentTransactionCommandHandler;
import com.mts.backend.application.payment.handler.CreatePaymentCommandHandler;
import com.mts.backend.application.payment.handler.CreatePaymentMethodCommandHandler;
import com.mts.backend.application.payment.handler.DeletePmtByIdCommandHandler;
import com.mts.backend.application.payment.handler.UpdatePaymentMethodCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component
public class PaymentCommandBus extends AbstractCommandBus {
    
    public PaymentCommandBus(CreatePaymentCommandHandler createPaymentCommandHandler,
                             CompletePaymentTransactionCommandHandler completePaymentTransactionCommandHandler,
                             CreatePaymentMethodCommandHandler createPaymentMethodCommandHandler, UpdatePaymentMethodCommandHandler updatePaymentMethodCommandHandler) {
        register(CreatePaymentCommand.class, createPaymentCommandHandler);
        register(CreatePaymentMethodCommand.class, createPaymentMethodCommandHandler);
        register(PaymentTransactionCommand.class, completePaymentTransactionCommandHandler);
        register(UpdatePaymentMethodCommand.class, updatePaymentMethodCommandHandler);
    }
}
