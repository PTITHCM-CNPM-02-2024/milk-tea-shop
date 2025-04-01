package com.mts.backend.domain.product.jpa.dto;

import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.value_object.CategoryName;

/**
 * Projection for {@link com.mts.backend.domain.product.CategoryEntity}
 */
public interface CategoryEntityInfo {
    CategoryId getId();

    CategoryName getName();

    String getDescription();
}