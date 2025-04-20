package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.UpdateCategoryCommand;
import com.mts.backend.domain.product.Category;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.jpa.JpaCategoryRepository;
import com.mts.backend.domain.product.value_object.CategoryName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateCategoryCommandHandler implements ICommandHandler<UpdateCategoryCommand, CommandResult> {

    private final JpaCategoryRepository categoryRepository;

    public UpdateCategoryCommandHandler(JpaCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * @param command
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(UpdateCategoryCommand command)  {
        Objects.requireNonNull(command, "UpdateCategoryCommand is required");

        try {
            var category = mustBeExistCategory(command.getId());

            category.setDescription(command.getDescription());
            category.setName(command.getName());

            return CommandResult.success(category.getId());
        } catch (Exception e) {
            if (e.getMessage().contains("uk_category_name")) {
               return CommandResult.notFoundFail("Tên danh mục " + command.getName().getValue() + " đã tồn tại");
            }
            throw new DomainException("Lỗi khi cập nhật danh mục"+ e.getMessage());
        }
    }

    private Category mustBeExistCategory(CategoryId categoryId) {
        Objects.requireNonNull(categoryId, "Category id is required");
        return categoryRepository.findById(categoryId.getValue())
                .orElseThrow(() -> new NotFoundException("Danh mục " + categoryId + " không tồn tại"));
    }

}
