package com.mts.backend.domain.payment.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import lombok.Value;
import org.hibernate.validator.constraints.Range;

@Value(staticConstructor = "of")
public class PaymentMethodId {

    @Range(min = 1, max = IdentifiableProvider.TINYINT_UNSIGNED_MAX)
    int value;

    public PaymentMethodId(int value) {
        this.value = value;
    }


    public static PaymentMethodId create() {
        return new PaymentMethodId(IdentifiableProvider.generateTimeBasedUnsignedTinyInt());
    }

}
    
    
