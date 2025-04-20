package com.mts.backend.domain.order.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;
import org.hibernate.validator.constraints.Range;

@Value(staticConstructor = "of")
public class OrderDiscountId {
    @Range(min = 1, max = IdentifiableProvider.INT_UNSIGNED_MAX)
    long value;

    public OrderDiscountId(long value) {
        this.value = value;
    }
    

    public static OrderDiscountId create() {
        return new OrderDiscountId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
    
}
