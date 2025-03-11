package com.mts.backend.domain.product.repository;

import com.mts.backend.domain.product.Category;
import com.mts.backend.domain.product.identifier.CategoryId;

import java.util.Optional;

public interface ICategoryRepository {
    boolean existsById(CategoryId categoryId);
    
    Category create(Category category);
    
    Category findParentCategory(CategoryId categoryId);
    
    Optional<Category> findById(CategoryId categoryId);
}
