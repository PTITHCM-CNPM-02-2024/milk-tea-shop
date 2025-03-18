package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.UpdateCategoryCommand;
import com.mts.backend.domain.product.Category;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.repository.ICategoryRepository;
import com.mts.backend.domain.product.value_object.CategoryName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateCategoryCommandHandler implements ICommandHandler<UpdateCategoryCommand, CommandResult> {
    
    private  final ICategoryRepository categoryRepository;
    
    public UpdateCategoryCommandHandler(ICategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * @param command 
     * @return
     */
    @Override
    public CommandResult handle(UpdateCategoryCommand command) {
        Objects.requireNonNull(command, "UpdateCategoryCommand is required");
        
        var category = mustBeExistCategory(CategoryId.of(command.getId()));
        
        category.changeDescription(command.getDescription());
        
        if (category.changeName(CategoryName.of(command.getName()))) {
            verifyUniqueName(category.getName());
        }
        
        if (command.getParentId() != null) {
            verifyParentCategoryIfSpecified(CategoryId.of(command.getParentId()));
            category.changeParentId(CategoryId.of(command.getParentId()));
        }else {
            category.changeParentId(null);
        }
        
        var updatedCategory = categoryRepository.save(category);
        
        return CommandResult.success(updatedCategory.getId().getValue());
    }

    
    private Category mustBeExistCategory(CategoryId categoryId) {
        Objects.requireNonNull(categoryId, "Category id is required");
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Danh mục " + categoryId + " không tồn tại"));
    }
    
    private void verifyUniqueName(CategoryName category) {
        Objects.requireNonNull(category, "Category name is required");
        
        categoryRepository.findByName(category).ifPresent(c -> {
            throw new DuplicateException("Category " + category.getValue() + " đã tồn tại");
        });
    }
    
    private void verifyParentCategoryIfSpecified(CategoryId parentId) {
        if (parentId != null && !categoryRepository.existsById(parentId)) {
            throw new NotFoundException("Danh mục cha " + parentId + " không tồn tại");
        }
    }
    
    
}
