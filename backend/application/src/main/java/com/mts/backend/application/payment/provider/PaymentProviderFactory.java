package com.mts.backend.application.payment.provider;

import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import org.springframework.stereotype.Service;

@Service
public class PaymentProviderFactory implements IPaymentProviderFactory{
    
    private final CashProvider cashProvider;
    
    public PaymentProviderFactory(CashProvider cashProvider) {
        this.cashProvider = cashProvider;
    }

    /**
     * @param id 
     * @return
     */
    @Override
    public IPaymentProvider getPaymentProvider(PaymentMethodId id) {

        if (cashProvider.getPaymentMethodId().equals(id)) {
            return cashProvider;
        }
        
        return  cashProvider;
    }
}
