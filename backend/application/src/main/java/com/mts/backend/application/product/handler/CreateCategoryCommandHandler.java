package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.CreateCategoryCommand;
import com.mts.backend.domain.product.Category;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.repository.ICategoryRepository;
import com.mts.backend.domain.product.value_object.CategoryName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CreateCategoryCommandHandler implements ICommandHandler<CreateCategoryCommand, CommandResult> {
    private final ICategoryRepository categoryRepository;

    public CreateCategoryCommandHandler(ICategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * @param command
     * @return
     */
    @Override
    public CommandResult handle(CreateCategoryCommand command) {
        Objects.requireNonNull(command, "Create category command is required");
        

        Category category = new Category(
                CategoryId.create(),
                CategoryName.of(command.getName()),
                command.getDescription().orElse(null),
                command.getParentId().map(CategoryId::of).orElse(null),
                command.getUpdatedAt().orElse(LocalDateTime.now())
        );
        
        verifyUniqueName(category.getName());
        
        verifyParentCategoryIfSpecified(category.getParentId().orElse(null));

        var createdCategory = categoryRepository.save(category);

        return CommandResult.success(createdCategory.getId().getValue());

    }

    private void verifyUniqueName(CategoryName name) {
        Objects.requireNonNull(name, "Category name is required");

        categoryRepository.findByName(name).ifPresent(c -> {
            throw new DuplicateException("Category " + name.getValue() + " đã tồn tại");
        });
    }
    
    private void verifyParentCategoryIfSpecified(CategoryId parentId) {
        if (parentId != null && !categoryRepository.existsById(parentId)) {
            throw new NotFoundException("Parent category " + parentId + " không tồn tại");
        }
        
    }


}
