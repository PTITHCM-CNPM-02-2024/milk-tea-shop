package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.UpdateProductPriceCommand;
import com.mts.backend.domain.product.Product;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.jpa.JpaProductRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateProductPriceCommandHandler implements ICommandHandler<UpdateProductPriceCommand, CommandResult> {
    private final JpaProductRepository productRepository;

    public UpdateProductPriceCommandHandler(JpaProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * @param command
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(UpdateProductPriceCommand command) {
        Objects.requireNonNull(command, "UpdateProductPriceCommand is required");

        try {
            var product = mustBeExistProduct(command.getProductId());

            for (var price : command.getProductPrices()) {
                product.setPrice(price.getSizeId(), price.getPrice());
            }
            
            productRepository.saveAndFlush(product);

            return CommandResult.success(product.getId());
        } catch (DataIntegrityViolationException e) {
            throw new DomainException("Lỗi khi cập nhật giá sản phẩm", e);
        }
    }

    private Product mustBeExistProduct(ProductId productId) {
        return productRepository.findById(productId.getValue())
                .orElseThrow(() -> new NotFoundException("Sản phẩm " + productId + " không tồn tại"));
    }
}
