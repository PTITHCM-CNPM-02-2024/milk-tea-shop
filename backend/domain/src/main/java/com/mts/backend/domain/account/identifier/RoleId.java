package com.mts.backend.domain.account.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import lombok.*;
import org.hibernate.validator.constraints.Range;


@Value(staticConstructor = "of")
public class RoleId  {
    @Range(min = 1, max = IdentifiableProvider.TINYINT_UNSIGNED_MAX)
    int value;
    
    public RoleId(@Range(min = 1, max = IdentifiableProvider.TINYINT_UNSIGNED_MAX) int value) {
        this.value = value;
    }
    
    public static RoleId create() {
        return new RoleId(IdentifiableProvider.generateTimeBasedUnsignedTinyInt());
    }
    
}
