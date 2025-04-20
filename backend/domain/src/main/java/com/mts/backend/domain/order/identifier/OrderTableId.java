package com.mts.backend.domain.order.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

@Value(staticConstructor = "of")
public class OrderTableId  {
    @Range(min = 1, max = IdentifiableProvider.INT_UNSIGNED_MAX)
    long value;
    
    public OrderTableId(@Range(min = 1, max = IdentifiableProvider.INT_UNSIGNED_MAX) long value) {
        this.value = value;
    }

    
    public static OrderTableId create(){
        return new OrderTableId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }

}
