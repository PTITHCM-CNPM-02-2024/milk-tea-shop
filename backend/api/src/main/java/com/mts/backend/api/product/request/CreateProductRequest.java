package com.mts.backend.api.product.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO for {@link com.mts.backend.domain.product.Product}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest implements Serializable {
    String name;
    String description;
    Integer categoryId;
    boolean available;
    boolean signature;
    String imagePath;
    Set<ProductPriceRequest> prices;

    /**
     * DTO for {@link com.mts.backend.domain.product.entity.ProductPrice}
     */
    @Data
    public static class ProductPriceRequest implements Serializable {
        Integer sizeIdValue;
        BigDecimal priceAmount;
    }
}