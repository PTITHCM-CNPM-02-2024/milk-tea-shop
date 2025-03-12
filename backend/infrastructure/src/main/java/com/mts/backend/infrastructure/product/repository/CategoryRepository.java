package com.mts.backend.infrastructure.product.repository;

import com.mts.backend.domain.product.Category;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.repository.ICategoryRepository;
import com.mts.backend.domain.product.value_object.CategoryName;
import com.mts.backend.infrastructure.persistence.entity.CategoryEntity;
import com.mts.backend.infrastructure.product.jpa.JpaCategoryRepository;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
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
        Objects.requireNonNull(category, "Category không được null");
        
        CategoryEntity parentEntity = null;
        
        if (category.getParentId().isPresent()) {
            parentEntity = CategoryEntity.builder()
                    .id(category.getParentId().get().getValue())
                    .build();
        }

        CategoryEntity categoryEntity = CategoryEntity.builder()
                .name(category.getName().getValue())
                .description(category.getDescription().orElse(null))
                .parentCategoryEntity(parentEntity)
                .id(null)
                .build();
        
        categoryEntity.setCreatedAt(category.getCreatedAt());
        categoryEntity.setUpdatedAt(category.getUpdatedAt());
        
        jpaCategoryRepository.save(categoryEntity);
        
        return new Category(
                CategoryId.of(categoryEntity.getId()),
                category.getName(),
                category.getDescription().orElse(null),
                category.getParentId().orElse(null),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }

    /**
     * @param categoryId 
     * @return
     */
    @Override
    public Optional<Category> findById(CategoryId categoryId) {
        
        Objects.requireNonNull(categoryId, "Category Id không được null");
        
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

    private void verifyUniqueName(CategoryName name) {
        jpaCategoryRepository.findByName(name.getValue())
                .ifPresent(entity -> {
                    CategoryName existingName = CategoryName.of(entity.getName());
                    if (name.equals(existingName)) {
                        throw new DuplicateException("Tên danh mục \"" + name.getValue() + "\" đã tồn tại");
                    }
                });
    }
    
    private Optional<CategoryEntity> verifyParentCategory(Optional<CategoryId> parentId) {
        return parentId.flatMap(categoryId -> jpaCategoryRepository.findById(categoryId.getValue())
                .or(() -> {
                    throw new NotFoundException("Không tìm thấy danh mục cha có ID " + categoryId.getValue());
                }));

    }
    
    private Category mapToDomain(CategoryEntity entity, Category originalCategory) {
        return new Category(
                CategoryId.of(entity.getId()),
                originalCategory.getName(),
                originalCategory.getDescription().orElse(null),
                originalCategory.getParentId().orElse(null),
                entity.getCreatedAt().orElse(null),
                entity.getUpdatedAt().orElse(null)
        );
    }
//    /**
//     * @param category
//     * @return
//     */
//    @Override
//    public Category create(Category category) {
//        try {
//
//            Optional<CategoryId> parentId = Objects.requireNonNull(category, "Category không được null").getParentId();
//
//
//            Optional<CategoryEntity> parentCategoryEntity = Optional.empty();
//            if (parentId.isPresent()) {
//
//                parentCategoryEntity = jpaCategoryRepository.findById(parentId.get().getValue());
//
//                if (parentCategoryEntity.isEmpty()) {
//                    throw new DomainException("Không tìm thấy danh mục cha");
//                }
//            }
//
//            Optional<CategoryEntity> existingCategory = jpaCategoryRepository.findByName(category.getName().getValue());
//            if (existingCategory.isPresent()) {
//                CategoryName existingName = CategoryName.of(existingCategory.get().getName());
//                if (category.getName().equals(existingName)) {
//                    throw new DomainException("Tên danh mục \"" + category.getName().getValue() + "\" đã tồn tại");
//                }
//            }
//
//            CategoryEntity categoryEntity = CategoryEntity.builder()
//                    .name(category.getName().getValue())
//                    .description(category.getDescription().orElse(null))
//                    .parentCategoryEntity(parentCategoryEntity.orElse(null))
//                    .build();
//
//            categoryEntity.setCreatedAt(category.getCreatedAt());
//            categoryEntity.setUpdatedAt(category.getUpdatedAt());
//
//            CategoryEntity createdCategoryEntity = jpaCategoryRepository.save(categoryEntity);
//
//            return new Category(
//                    CategoryId.of(createdCategoryEntity.getId()),
//                    category.getName(),
//                    category.getDescription().orElse(null),
//                    category.getParentId().orElse(null),
//                    category.getCreatedAt(),
//                    category.getUpdatedAt()
//            );
//        } catch (Exception e) {
//            throw new DomainException(e.getMessage());
//        }
//    }
//
//    /**
//     * @param categoryId
//     * @return
//     */
//    @Override
//    public Category findParentCategory(CategoryId categoryId) {
//        return null;
//    }
//
//    /**
//     * @param categoryId
//     * @return
//     */
//    @Override
//    public Optional<Category> findById(CategoryId categoryId) {
//        Objects.requireNonNull(categoryId, "Category Id không được null");
//        try {
//            Optional<CategoryEntity> categoryEntity = jpaCategoryRepository.findById(categoryId.getValue());
//
//            if (categoryEntity.isEmpty()) {
//                return Optional.empty();
//            }
//
//            return Optional.of(new Category(
//                    CategoryId.of(categoryEntity.get().getId()),
//                    CategoryName.of(categoryEntity.get().getName()),
//                    categoryEntity.get().getDescription(),
//                    categoryEntity.get().getParentCategoryEntity() == null ? null : CategoryId.of(categoryEntity.get().getParentCategoryEntity().getId()),
//                    categoryEntity.get().getCreatedAt().orElse(null),
//                    categoryEntity.get().getUpdatedAt().orElse(null)
//            ));
//        } catch (Exception e) {
//            throw new DomainException(e.getMessage());
//        }
//    }
}
