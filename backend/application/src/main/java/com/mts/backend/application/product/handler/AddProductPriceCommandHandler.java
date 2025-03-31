package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.AddProductPriceCommand;
import com.mts.backend.domain.product.ProductEntity;
import com.mts.backend.domain.product.ProductPriceEntity;
import com.mts.backend.domain.product.ProductSizeEntity;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.identifier.ProductPriceId;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.jpa.JpaProductRepository;
import com.mts.backend.domain.product.jpa.JpaProductSizeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AddProductPriceCommandHandler implements ICommandHandler<AddProductPriceCommand, CommandResult> {

    private final JpaProductRepository productRepository;

    private final JpaProductSizeRepository sizeRepository;

    public AddProductPriceCommandHandler(
            JpaProductRepository productRepository,
            JpaProductSizeRepository sizeRepository) {
        this.productRepository = productRepository;
        this.sizeRepository = sizeRepository;
    }

    /**
     * @param command
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(AddProductPriceCommand command) {
        Objects.requireNonNull(command, "AddProductPriceCommand is required");

        var product = mustBeExistProduct(command.getProductId());

        for (var price : command.getProductPrices()) {
            
            var size = mustBeExistProductSize(price.getSizeId());

            product.addProductPriceEntity(ProductPriceEntity.builder()
                    .id(ProductPriceId.create().getValue())
                    .productEntity(product)
                    .size(size)
                    .price(price.getPrice())
                    .build());
        }
        
        return CommandResult.success(product.getId());
    }

    private ProductEntity mustBeExistProduct(ProductId productId) {
        Objects.requireNonNull(productId, "ProductId is required");
        return productRepository.findById(productId.getValue()).orElseThrow(() -> new NotFoundException("Sản phẩm " + productId + " không tồn tại"));
    }

    private ProductSizeEntity mustBeExistProductSize(ProductSizeId sizeId) {
        Objects.requireNonNull(sizeId, "ProductSizeId is required");

        if (!sizeRepository.existsById(sizeId.getValue())) {
            throw new NotFoundException("Kích thước " + sizeId + " không tồn tại");
        }
        
        return sizeRepository.getReferenceById(sizeId.getValue());
    }
}
