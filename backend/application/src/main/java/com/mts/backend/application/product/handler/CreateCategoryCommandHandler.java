package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.CreateCategoryCommand;
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
public class CreateCategoryCommandHandler implements ICommandHandler<CreateCategoryCommand, CommandResult> {
    private final JpaCategoryRepository categoryRepository;

    public CreateCategoryCommandHandler(JpaCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * @param command
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(CreateCategoryCommand command) {
        Objects.requireNonNull(command, "Create category command is required");
        
        verifyUniqueName(command.getName());
        
        var par = verifyParentCategoryIfSpecified(command.getParentId().orElse(null));
        
        var category = CategoryEntity.builder()
                .id(CategoryId.create().getValue())
                .name(command.getName())
                .description(command.getDescription().orElse(null))
                .parentCategoryEntity(par)
                .build();

        var createdCategory = categoryRepository.save(category);

        return CommandResult.success(createdCategory.getId());

    }

    private void verifyUniqueName(CategoryName name) {
        Objects.requireNonNull(name, "Category name is required");

        categoryRepository.findByName(name).ifPresent(c -> {
            throw new DuplicateException("Category " + name.getValue() + " đã tồn tại");
        });
    }
    
    private CategoryEntity verifyParentCategoryIfSpecified(CategoryId parentId) {
        
        if (parentId == null) {
            return null;
        }
        
        if (!categoryRepository.existsById(parentId.getValue())) {
            throw new NotFoundException("Danh mục cha " + parentId.getValue() + " không tồn tại");
        }
        
        return categoryRepository.getReferenceById(parentId.getValue());
    }


}
