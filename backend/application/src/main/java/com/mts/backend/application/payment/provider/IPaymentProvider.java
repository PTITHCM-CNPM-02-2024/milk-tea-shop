package com.mts.backend.application.payment.provider;

import com.mts.backend.application.payment.command.PaymentTransactionCommand;
import com.mts.backend.application.payment.response.PaymentInitResponse;
import com.mts.backend.application.payment.response.PaymentResult;
import com.mts.backend.domain.order.Order;
import com.mts.backend.domain.payment.Payment;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;

public interface IPaymentProvider {
    PaymentMethodId getPaymentMethodId();
    
    PaymentInitResponse initPayment(Payment payment, Order order);
    
    PaymentResult dispatch(Payment payment, Order order, PaymentTransactionCommand transactionCommand);
}
