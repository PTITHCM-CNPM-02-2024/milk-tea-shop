package com.mts.backend.domain.product.identifier;

import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.shared.domain.Identifiable;

import java.util.Objects;
import java.util.UUID;

public class ProductSizeId implements Identifiable {
    private final int value;

    private ProductSizeId(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Size id phải lớn hơn 0");
        }
        if (value > IdentifiableProvider.SMALLINT_UNSIGNED_MAX){
            throw new IllegalArgumentException("Size id phải nhỏ hơn " + IdentifiableProvider.SMALLINT_UNSIGNED_MAX);
        }
        this.value = value;
    }


    public static ProductSizeId of(int value) {
        return new ProductSizeId(value);
    }

    public static ProductSizeId of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Size id cannot be null");
        }
        return new ProductSizeId(Integer.parseInt(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductSizeId productSizeId = (ProductSizeId) o;

        return Objects.equals(value, productSizeId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public int getValue() {
        return value;
    }

    /**
     * TODO: Đây là phương thức cần kiểm ra lại
     */
    public static ProductSizeId create() {
        return new ProductSizeId(IdentifiableProvider.generateTimeBasedUnsignedSmallInt());
    }
}
