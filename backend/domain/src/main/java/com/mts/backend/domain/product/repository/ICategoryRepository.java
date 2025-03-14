package com.mts.backend.domain.product.repository;

import com.mts.backend.domain.product.Category;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.value_object.CategoryName;

import java.util.List;
import java.util.Optional;

public interface ICategoryRepository {
    boolean existsById(CategoryId categoryId);
    
    Category create(Category category);

    Optional<Category> findById(CategoryId categoryId);
    
    Optional<Category> findByName(CategoryName name);
    
    List<Category> findAll();
    
    Category save(Category category);
}
