package com.mts.backend.application.product.handler;

import org.springframework.stereotype.Service;

import com.mts.backend.application.product.command.DeleteCatByIdCommand;
import com.mts.backend.domain.product.jpa.JpaCategoryRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
@Service
public class DeleteCatByIdCommandHandler implements ICommandHandler<DeleteCatByIdCommand, CommandResult> {
    private final JpaCategoryRepository categoryRepository;

    public DeleteCatByIdCommandHandler(JpaCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public CommandResult handle(DeleteCatByIdCommand command) {
        var category = categoryRepository.findById(command.getId().getValue()).orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục " + command.getId().getValue()));
        categoryRepository.delete(category);
        return CommandResult.success(category.getId());
    }

}
