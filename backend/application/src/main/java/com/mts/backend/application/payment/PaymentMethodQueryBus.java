package com.mts.backend.application.payment;

import com.mts.backend.application.payment.query.DefaultPaymentMethodQuery;
import com.mts.backend.application.payment.query_handler.GetAllPaymentMethodCommandHandler;
import com.mts.backend.shared.command.AbstractCommandBus;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodQueryBus extends AbstractQueryBus {
    
    public PaymentMethodQueryBus(GetAllPaymentMethodCommandHandler getAllPaymentMethodCommandHandler) {
        register(DefaultPaymentMethodQuery.class, getAllPaymentMethodCommandHandler);
    }
}
