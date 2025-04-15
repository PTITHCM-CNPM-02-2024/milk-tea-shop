package com.mts.backend.application.product.handler;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.mts.backend.application.product.command.DeleteProductByIdCommand;
import com.mts.backend.domain.product.jpa.JpaProductRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;

@Service
public class DeleteProductByIdCommandHandler implements ICommandHandler<DeleteProductByIdCommand, CommandResult> {
    private final JpaProductRepository productRepository;

    public DeleteProductByIdCommandHandler(JpaProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public CommandResult handle(DeleteProductByIdCommand command) {
        Objects.requireNonNull(command, "DeleteProductByIdCommand is null");
        var product = productRepository.findById(command.getId().getValue())
                .orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại"));
        productRepository.delete(product);
        return CommandResult.success(product.getId());
    }
}
