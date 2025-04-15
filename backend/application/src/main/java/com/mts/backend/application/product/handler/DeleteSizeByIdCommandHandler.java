package com.mts.backend.application.product.handler;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.mts.backend.application.product.command.DeleteSizeByIdCommand;
import com.mts.backend.domain.product.jpa.JpaProductSizeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;

@Service
public class DeleteSizeByIdCommandHandler implements ICommandHandler<DeleteSizeByIdCommand, CommandResult> {
    private final JpaProductSizeRepository productSizeRepository;

    public DeleteSizeByIdCommandHandler(JpaProductSizeRepository productSizeRepository) {
        this.productSizeRepository = productSizeRepository;
    }

    @Override
    public CommandResult handle(DeleteSizeByIdCommand command) {
        Objects.requireNonNull(command, "DeleteSizeByIdCommand is null");
        var productSize = productSizeRepository.findById(command.getId().getValue())
                .orElseThrow(() -> new NotFoundException("Kích thước không tồn tại"));
        productSizeRepository.delete(productSize);
        return CommandResult.success();
    }
}