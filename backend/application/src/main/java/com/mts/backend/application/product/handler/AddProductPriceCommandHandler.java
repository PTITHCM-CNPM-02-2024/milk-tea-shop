package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.AddProductPriceCommand;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.product.Product;
import com.mts.backend.domain.product.entity.ProductPrice;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.identifier.ProductPriceId;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.repository.IProductRepository;
import com.mts.backend.domain.product.repository.ISizeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class AddProductPriceCommandHandler implements ICommandHandler<AddProductPriceCommand, CommandResult> {

    private final IProductRepository productRepository;

    private final ISizeRepository sizeRepository;

    public AddProductPriceCommandHandler(IProductRepository productRepository, ISizeRepository sizeRepository) {
        this.productRepository = productRepository;
        this.sizeRepository = sizeRepository;
    }

    /**
     * @param command
     * @return
     */
    @Override
    public CommandResult handle(AddProductPriceCommand command) {
        Objects.requireNonNull(command, "AddProductPriceCommand is required");

        var product = mustBeExistProduct(ProductId.of(command.getProductId()));

        for (var price : command.getProductPrices()) {
            mustBeExistProductSize(ProductSizeId.of(price.getSizeId()));

            product.addPrice(
                    new ProductPrice(
                            ProductPriceId.create(),
                            product.getId(),
                            ProductSizeId.of(price.getSizeId()),
                            Money.of(price.getPrice()),
                            LocalDateTime.now()
                    )
            );
        }

        var updatedProduct = productRepository.save(product);
        
        return CommandResult.success(updatedProduct.getId().getValue());
    }

    private Product mustBeExistProduct(ProductId productId) {
        return productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Sản phẩm " + productId + " không tồn tại"));
    }

    private void mustBeExistProductSize(ProductSizeId sizeId) {
        Objects.requireNonNull(sizeId, "ProductSizeId is required");

        if (!sizeRepository.existsById(sizeId)) {
            throw new NotFoundException("Kích thước " + sizeId + " không tồn tại");
        }
    }
}
