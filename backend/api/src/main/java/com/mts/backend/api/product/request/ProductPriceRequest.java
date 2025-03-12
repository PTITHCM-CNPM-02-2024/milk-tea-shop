package com.mts.backend.api.product.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link com.mts.backend.domain.product.entity.ProductPrice}
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductPriceRequest implements Serializable {
    Integer sizeId;
    BigDecimal price;
}