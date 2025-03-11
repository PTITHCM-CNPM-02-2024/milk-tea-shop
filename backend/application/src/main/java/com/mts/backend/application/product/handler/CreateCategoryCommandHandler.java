package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.CreateCategoryCommand;
import com.mts.backend.domain.product.Category;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.repository.ICategoryRepository;
import com.mts.backend.domain.product.value_object.CategoryName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import org.springframework.stereotype.Component;

@Component
public class CreateCategoryCommandHandler implements ICommandHandler<CreateCategoryCommand, CommandResult> {
    
    
    /**
     * @param command 
     * @return
     */
    @Override
    public CommandResult handle(CreateCategoryCommand command) {
        try {

            Category category = new Category(
                    CategoryId.create(),
                    CategoryName.of(command.getName()),
                    command.getDescription().orElse(null),
                    command.getParentId().map(CategoryId::of).orElse(null),
                    command.getCreatedAt(),
                    command.getUpdatedAt()
            );
            
            var createdCategory = categoryRepository.create(category);
            
            return CommandResult.success(createdCategory.getId().getValue());
            
        }catch(DomainException e){
            return CommandResult.businessFail(e.getMessage());
        }
        catch (Exception e) {
            return CommandResult.systemFail(e.getMessage());
        }
    }

    private final ICategoryRepository categoryRepository;
    
    public CreateCategoryCommandHandler(ICategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    
}
