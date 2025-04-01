package com.mts.backend.application.payment.provider;

import com.mts.backend.application.payment.command.PaymentTransactionCommand;
import com.mts.backend.application.payment.response.PaymentInitResponse;
import com.mts.backend.application.payment.response.PaymentResult;
import com.mts.backend.domain.order.OrderEntity;
import com.mts.backend.domain.payment.PaymentEntity;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;

public interface IPaymentProvider {
    PaymentMethodId getPaymentMethodId();
    
    PaymentInitResponse initPayment(PaymentEntity payment, OrderEntity order);
    
    PaymentResult dispatch(PaymentEntity payment, OrderEntity order, PaymentTransactionCommand transactionCommand);
}
