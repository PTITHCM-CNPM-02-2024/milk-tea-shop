package com.mts.backend.domain.account.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import lombok.Value;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;
import java.util.Objects;
@Value(staticConstructor = "of")
public class AccountId {
    @Range(min = 1, max = IdentifiableProvider.INT_UNSIGNED_MAX)
    long value;

    public AccountId(long value) {
        this.value = value;
    }

    public static AccountId create() {
        return new AccountId(IdentifiableProvider.generateTimeBasedUnsignedInt());
    }
}