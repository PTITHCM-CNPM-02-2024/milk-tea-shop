package com.mts.backend.api.product.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

/**
 * DTO for {@link com.mts.backend.domain.product.entity.ProductPrice}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddProductPriceRequest {
    ArrayList<ProductPriceRequest> productPrices;
}
