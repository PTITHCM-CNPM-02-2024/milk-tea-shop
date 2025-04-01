package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.DeletePriceBySizeIdCommand;
import com.mts.backend.domain.product.ProductEntity;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.jpa.JpaProductRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class DeletePriceBySizeIdCommandHandler implements ICommandHandler<DeletePriceBySizeIdCommand, CommandResult> {

    private final JpaProductRepository productRepository;

    public DeletePriceBySizeIdCommandHandler(JpaProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public CommandResult handle(DeletePriceBySizeIdCommand command) {
        Objects.requireNonNull(command, "DeletePriceBySizeIdCommand is required");

        var product = mustBeExistProduct(command.getProductId());

        if (product.removeProductPriceEntity(command.getSizeId())) {
            return CommandResult.success(product.getId());
        }

        return CommandResult.notFoundFail("Không tìm thấy kích thước " + command.getSizeId() + " trong sản phẩm " + command.getProductId());
    }

    private ProductEntity mustBeExistProduct(ProductId productId) {
        return productRepository.findById(productId.getValue())
                .orElseThrow(() -> new NotFoundException("Sản phẩm " + productId + " không tồn tại"));
    }


}
