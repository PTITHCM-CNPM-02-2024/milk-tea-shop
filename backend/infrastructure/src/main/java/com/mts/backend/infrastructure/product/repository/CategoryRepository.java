package com.mts.backend.infrastructure.product.repository;

import com.mts.backend.domain.product.Category;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.repository.ICategoryRepository;
import com.mts.backend.domain.product.value_object.CategoryName;
import com.mts.backend.infrastructure.persistence.entity.CategoryEntity;
import com.mts.backend.infrastructure.product.jpa.JpaCategoryRepository;
import com.mts.backend.shared.exception.DomainException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Component
public class CategoryRepository implements ICategoryRepository{
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
        return false;
    }

    /**
     * @param category 
     * @return
     */
    @Override
    public Category create(Category category) {
        try {
            Objects.requireNonNull(category, "Category không được null");

            Optional<CategoryId> parentId = category.getParentId();
            
            Optional<CategoryEntity> parentCategoryEntity       = Optional.empty();    
            if (parentId.isPresent()) {
                
                parentCategoryEntity = jpaCategoryRepository.findById(parentId.get().getValue());
                
                if (parentCategoryEntity.isEmpty()) {
                    throw new DomainException("Không tìm thấy danh mục cha");
                }
                
            }
            
            CategoryEntity categoryEntity = CategoryEntity.builder()
                    .name(category.getName().getValue())
                    .description(category.getDescription().orElse(null))
                    .parentCategoryEntity(parentCategoryEntity.orElse(null))
                    .build();
            
            categoryEntity.setCreatedAt(category.getCreatedAt().orElse(LocalDateTime.now()));
            categoryEntity.setUpdatedAt(category.getUpdatedAt().orElse(LocalDateTime.now()));
            
            CategoryEntity createdCategoryEntity = jpaCategoryRepository.save(categoryEntity);
            
            return new Category(
                    CategoryId.of(createdCategoryEntity.getId()),
                    category.getName(),
                    category.getDescription().orElse(null),
                    category.getParentId().orElse(null),
                    category.getCreatedAt().orElse(LocalDateTime.now()),
                    category.getUpdatedAt().orElse(LocalDateTime.now())
            );
        } catch (Exception e){
            throw new DomainException(e.getMessage());
        }
    }

    /**
     * @param categoryId 
     * @return
     */
    @Override
    public Category findParentCategory(CategoryId categoryId) {
        return null;
    }

    /**
     * @param categoryId 
     * @return
     */
    @Override
    public Optional<Category> findById(CategoryId categoryId) {
        Objects.requireNonNull(categoryId, "Category Id không được null");
        try{
            Optional<CategoryEntity> categoryEntity = jpaCategoryRepository.findById(categoryId.getValue());
            
            if (categoryEntity.isEmpty()) {
                return Optional.empty();
            }
            
            return Optional.of(new Category(
                    CategoryId.of(categoryEntity.get().getId()),
                    CategoryName.of(categoryEntity.get().getName()),
                    categoryEntity.get().getDescription(),
                    categoryEntity.get().getParentCategoryEntity() == null ? null : CategoryId.of(categoryEntity.get().getParentCategoryEntity().getId()),
                    categoryEntity.get().getCreatedAt().orElse(null),
                    categoryEntity.get().getUpdatedAt().orElse(null)
                    ));
        } catch (Exception e) {
            throw new DomainException(e.getMessage());
        }
    }
}
