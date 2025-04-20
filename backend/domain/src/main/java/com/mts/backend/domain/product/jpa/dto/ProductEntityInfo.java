package com.mts.backend.domain.product.jpa.dto;

import com.mts.backend.domain.product.Product;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.value_object.ProductName;

import java.util.Set;

/**
 * Projection for {@link Product}
 */
public interface ProductEntityInfo {
    ProductId getId();

    ProductName getName();

    String getDescription();

    Boolean isAvailable();

    Boolean isSignature();

    String getImagePath();

    CategoryEntityInfo getCategoryEntity();

    Set<ProductPriceEntityInfo> getProductPrices();
}