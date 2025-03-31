package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.UpdateCategoryCommand;
import com.mts.backend.domain.product.CategoryEntity;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.jpa.JpaCategoryRepository;
import com.mts.backend.domain.product.value_object.CategoryName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateCategoryCommandHandler implements ICommandHandler<UpdateCategoryCommand, CommandResult> {
    
    private  final JpaCategoryRepository categoryRepository;
    
    public UpdateCategoryCommandHandler(JpaCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * @param command 
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(UpdateCategoryCommand command) {
        Objects.requireNonNull(command, "UpdateCategoryCommand is required");
        
        var category = mustBeExistCategory(command.getId());
        
        category.setDescription(command.getDescription());
        
        if (category.changeName(command.getName())) {
            verifyUniqueName(category.getName());
        }
        
        var parentCategory = verifyParentCategory(command.getParentId().orElse(null));
        
        category.setParentCategoryEntity(parentCategory);
        
        var updatedCategory = categoryRepository.save(category);
        
        return CommandResult.success(updatedCategory.getId());
    }

    
    private CategoryEntity mustBeExistCategory(CategoryId categoryId) {
        Objects.requireNonNull(categoryId, "Category id is required");
        return categoryRepository.findById(categoryId.getValue())
                .orElseThrow(() -> new NotFoundException("Danh mục " + categoryId + " không tồn tại"));
    }
    
    private void verifyUniqueName(CategoryName category) {
        Objects.requireNonNull(category, "Category name is required");
        
        categoryRepository.findByName(category).ifPresent(c -> {
            throw new DuplicateException("Category " + category.getValue() + " đã tồn tại");
        });
    }
    
    private CategoryEntity verifyParentCategory(CategoryId parentId){
        if (Objects.isNull(parentId)) {
            return null;
        }
        
        if (!categoryRepository.existsById(parentId.getValue())) {
            throw new NotFoundException("Danh mục cha " + parentId + " không tồn tại");
        }
        
        return categoryRepository.getReferenceById(parentId.getValue());
    }
    
    
}
