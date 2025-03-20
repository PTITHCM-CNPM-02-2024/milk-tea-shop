package com.mts.backend.api.product.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link com.mts.backend.domain.product.entity.ProductPrice}
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
public class ProductPriceRequest implements Serializable {
    Integer sizeId;
    @EqualsAndHashCode.Exclude
    BigDecimal price;
}