package com.mts.backend.domain.product;

import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.value_object.CategoryName;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import com.mts.backend.shared.exception.DomainBusinessLogicException;
import com.mts.backend.shared.exception.DomainException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Category extends AbstractAggregateRoot<CategoryId>{
    private CategoryName name;
    
    private String description;
    
    private CategoryId parentId;
    
    private final LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    /**
     * @param categoryId
     */
    public Category(@NonNull CategoryId categoryId, @NonNull CategoryName name, @Nullable String description,@Nullable CategoryId parentId,@Nullable LocalDateTime createdAt,@Nullable LocalDateTime updatedAt) {
        super(categoryId);
        
        Objects.requireNonNull(name, "Tên danh mục không được null");
        if (parentId != null && parentId.equals(getId())) {
            throw new DomainException("Không thể chọn" + parentId + "làm cha của chính nó");
        }
        
        this.name = name;
        this.description = description == null ? "" : description;
        this.parentId = parentId;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
        this.updatedAt = updatedAt == null ? LocalDateTime.now() : updatedAt;
    }
    
    public void changeName(CategoryName name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void changeName(String name) {
        CategoryName newName = (CategoryName) checkAndAssign(CategoryName.create(name));
        if (!businessErrors.isEmpty()){
            throw new DomainBusinessLogicException(businessErrors);
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    public void changeDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void changeParentId(CategoryId parentId) {
        if (parentId != null && parentId.equals(getId())) {
            throw new DomainException("Không thể chọn" + parentId + "làm cha của chính nó");
        }
        
        this.parentId = parentId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public CategoryName getName() {
        return name;
    }
    
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
    
    public Optional<CategoryId> getParentId() {
        return Optional.ofNullable(parentId);
    }
    
    public Optional<LocalDateTime> getCreatedAt() {
        return Optional.ofNullable(createdAt);
    }
    
    public Optional<LocalDateTime> getUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }
    
    
}
