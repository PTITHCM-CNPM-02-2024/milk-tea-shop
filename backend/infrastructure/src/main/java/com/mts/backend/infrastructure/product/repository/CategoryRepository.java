package com.mts.backend.infrastructure.product.repository;

import com.mts.backend.domain.product.Category;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.repository.ICategoryRepository;
import com.mts.backend.domain.product.value_object.CategoryName;
import com.mts.backend.infrastructure.persistence.entity.CategoryEntity;
import com.mts.backend.infrastructure.product.jpa.JpaCategoryRepository;
import com.mts.backend.shared.exception.DomainException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class CategoryRepository implements ICategoryRepository {
    private final JpaCategoryRepository jpaCategoryRepository;

    public CategoryRepository(JpaCategoryRepository jpaCategoryRepository) {
        this.jpaCategoryRepository = jpaCategoryRepository;
    }

    /**
     * @param categoryId
     * @return
     */
    @Override
    public boolean existsById(CategoryId categoryId) {
        Objects.requireNonNull(categoryId, "CategoryId is required");
        return jpaCategoryRepository.existsById(categoryId.getValue());
    }
    
    @Override
    public Category create(Category category) {
        Objects.requireNonNull(category, "Category is required");
        
        CategoryEntity entity = CategoryEntity.builder()
                .name(category.getName().getValue())
                .description(category.getDescription().orElse(""))
                .parentCategoryEntity(category.getParentId().map(parentId -> CategoryEntity.builder().id(parentId.getValue()).build()).orElse(null))
                .id(category.getId().getValue())
                .build();
        
        jpaCategoryRepository.insertCategory(entity);
        
        return category;
    }

    /**
     * @param categoryId 
     * @return
     */
    @Override
    public Optional<Category> findById(CategoryId categoryId) {
        
        Objects.requireNonNull(categoryId, "Category ID is required");
        
        return jpaCategoryRepository.findById(categoryId.getValue())
                .map(entity -> new Category(
                        CategoryId.of(entity.getId()),
                        CategoryName.of(entity.getName()),
                        entity.getDescription(),
                        entity.getParentCategoryEntity() == null ? null : CategoryId.of(entity.getParentCategoryEntity().getId()),
                        entity.getCreatedAt().orElse(null),
                        entity.getUpdatedAt().orElse(null)
                ));
        
    }

    /**
     * @param name 
     * @return
     */
    @Override
    public Optional<Category> findByName(CategoryName name) {
        Objects.requireNonNull(name, "Category name is required");
        
        return jpaCategoryRepository.findByName(name.getValue())
                .map(entity -> new Category(
                        CategoryId.of(entity.getId()),
                        CategoryName.of(entity.getName()),
                        entity.getDescription(),
                        entity.getParentCategoryEntity() == null ? null : CategoryId.of(entity.getParentCategoryEntity().getId()),
                        entity.getCreatedAt().orElse(null),
                        entity.getUpdatedAt().orElse(null)
                ));
    }

    @Transactional
    public Category save(Category category){
        Objects.requireNonNull(category, "Category is required");
        
        try {
            if (jpaCategoryRepository.existsById(category.getId().getValue())){
                return update(category);
            }else {
                return create(category);
            }
        }catch (Exception e) {
            throw new DomainException("Không thể lưu danh mục", e);
        }
    }
        
    @Transactional
    protected Category update(Category category) {
        Objects.requireNonNull(category, "Category is required");
        
        CategoryEntity entity = CategoryEntity.builder()
                .id(category.getId().getValue())
                .name(category.getName().getValue())
                .description(category.getDescription().orElse(""))
                .parentCategoryEntity(category.getParentId().map(parentId -> CategoryEntity.builder().id(parentId.getValue()).build()).orElse(null))
                .build();
        
        jpaCategoryRepository.updateCategory(entity);
        
        return category;
    }
}
