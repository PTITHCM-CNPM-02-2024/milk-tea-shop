package com.mts.backend.application.payment;

import com.mts.backend.application.payment.query.DefaultPaymentMethodQuery;
import com.mts.backend.application.payment.query.DefaultPaymentQuery;
import com.mts.backend.application.payment.query.PaymentMethodByIdQuery;
import com.mts.backend.application.payment.query_handler.GetAllPaymentMethodCommandHandler;
import com.mts.backend.application.payment.query_handler.GetAllPaymentQueryHandler;
import com.mts.backend.application.payment.query_handler.GetPaymentMethodByIdQueryHandler;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodQueryBus extends AbstractQueryBus {
    
    public PaymentMethodQueryBus(GetAllPaymentMethodCommandHandler getAllPaymentMethodCommandHandler, GetPaymentMethodByIdQueryHandler getPaymentMethodByIdQueryHandler, GetAllPaymentQueryHandler getAllPaymentQueryHandler) {
        register(DefaultPaymentMethodQuery.class, getAllPaymentMethodCommandHandler);
        register(PaymentMethodByIdQuery.class, getPaymentMethodByIdQueryHandler);
        register(DefaultPaymentQuery.class, getAllPaymentQueryHandler);
    }
}
