package com.mts.backend.domain.product.jpa.dto;

import com.mts.backend.domain.product.Category;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.value_object.CategoryName;

/**
 * Projection for {@link Category}
 */
public interface CategoryEntityInfo {
    CategoryId getId();

    CategoryName getName();

    String getDescription();
}