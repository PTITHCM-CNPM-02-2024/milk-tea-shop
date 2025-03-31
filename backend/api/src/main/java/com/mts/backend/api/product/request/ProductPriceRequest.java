package com.mts.backend.api.product.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductPriceRequest implements Serializable {
    Integer sizeId;
    BigDecimal price;
}