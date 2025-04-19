package com.mts.backend.domain.customer.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import jakarta.persistence.AttributeConverter;
import lombok.Value;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

@Value(staticConstructor = "of")
public class MembershipTypeId implements Serializable {
    @Range(min = 0, max = IdentifiableProvider.TINYINT_UNSIGNED_MAX)
    int value;

    public MembershipTypeId(@Range(min = 0, max = IdentifiableProvider.TINYINT_UNSIGNED_MAX) int value) {
        this.value = value;
    }
    

    public static MembershipTypeId create() {
        return new MembershipTypeId(IdentifiableProvider.generateTimeBasedUnsignedTinyInt());
    }

}
