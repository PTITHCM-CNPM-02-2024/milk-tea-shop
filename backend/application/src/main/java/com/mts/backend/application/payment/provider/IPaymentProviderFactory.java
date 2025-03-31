package com.mts.backend.application.payment.provider;

import com.mts.backend.domain.payment.identifier.PaymentMethodId;

public interface IPaymentProviderFactory {
    
    IPaymentProvider getPaymentProvider(PaymentMethodId id);
}
