package com.mts.backend.application.payment;

import com.mts.backend.application.payment.query.PaymentReportByMonthQuery;
import com.mts.backend.application.payment.query_handler.GetPaymentReportByMonthQueryHandler;
import com.mts.backend.application.payment.response.PaymentReportResponse;
import org.springframework.stereotype.Component;

import com.mts.backend.application.payment.query.DefaultPaymentQuery;
import com.mts.backend.application.payment.query_handler.GetAllPaymentQueryHandler;
import com.mts.backend.application.payment.query_handler.GetPaymentByIdQueryHandler;
import com.mts.backend.shared.query.AbstractQueryBus;
import com.mts.backend.application.payment.query.PaymentByIdQuery;

@Component
public class PaymentQueryBus extends AbstractQueryBus {

    public PaymentQueryBus(GetAllPaymentQueryHandler getAllPaymentQueryHandler,
                           GetPaymentByIdQueryHandler getPaymentByIdQueryHandler,
        GetPaymentReportByMonthQueryHandler paymentReportByMonthQuery) {
        register(DefaultPaymentQuery.class, getAllPaymentQueryHandler);
        register(PaymentByIdQuery.class, getPaymentByIdQueryHandler);
        register(PaymentReportByMonthQuery.class, paymentReportByMonthQuery);
    }
}
