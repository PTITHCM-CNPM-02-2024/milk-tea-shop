package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.CreateCategoryCommand;
import com.mts.backend.domain.product.Category;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.jpa.JpaCategoryRepository;
import com.mts.backend.domain.product.value_object.CategoryName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;
import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Objects;

@Slf4j
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
                
        try{
            var category = Category.builder()
                .id(CategoryId.create().getValue())
                .name(command.getName())
                .description(command.getDescription().orElse(null))
                .build();

        var createdCategory = categoryRepository.saveAndFlush(category);

        return CommandResult.success(createdCategory.getId());
        }catch(DataIntegrityViolationException e){
            if (e.getMessage().contains("uk_category_name")) {
                throw new DuplicateException("Tên danh mục " + command.getName().getValue() + " đã tồn tại");
            }
            throw new DomainException("Lỗi khi tạo danh mục %s".formatted(command.getName().getValue()));
        }

    }
    

}
