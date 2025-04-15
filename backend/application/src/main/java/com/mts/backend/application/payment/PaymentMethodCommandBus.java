package com.mts.backend.application.payment;

import com.mts.backend.application.payment.command.CreatePaymentMethodCommand;
import com.mts.backend.application.payment.command.DeletePmtByIdCommand;
import com.mts.backend.application.payment.command.UpdatePaymentMethodCommand;
import com.mts.backend.application.payment.handler.CreatePaymentMethodCommandHandler;
import com.mts.backend.application.payment.handler.DeletePmtByIdCommandHandler;
import com.mts.backend.application.payment.handler.UpdatePaymentMethodCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import org.springframework.stereotype.Component;

@Component  
public class PaymentMethodCommandBus extends AbstractCommandBus {
    
    public PaymentMethodCommandBus(CreatePaymentMethodCommandHandler createPaymentMethodCommandHandler,
                                   UpdatePaymentMethodCommandHandler updatePaymentMethodCommandHandler,
                                   DeletePmtByIdCommandHandler deletePmtByIdCommandHandler){
        register(CreatePaymentMethodCommand.class, createPaymentMethodCommandHandler);
        register(UpdatePaymentMethodCommand.class, updatePaymentMethodCommandHandler);
        register(DeletePmtByIdCommand.class, deletePmtByIdCommandHandler);
    }
}
