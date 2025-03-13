package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.UpdateProductPriceCommand;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.product.Product;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.repository.IProductRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateProductPriceCommandHandler implements ICommandHandler<UpdateProductPriceCommand, CommandResult> {
    private final IProductRepository productRepository;
    
    public UpdateProductPriceCommandHandler(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    public CommandResult handle(UpdateProductPriceCommand command) {
        Objects.requireNonNull(command, "UpdateProductPriceCommand is required");
        
        var product = mustBeExistProduct(ProductId.of(command.getProductId()));
        
        for (var price : command.getProductPrices()){
            product.changePrice(ProductSizeId.of(price.getSizeId()), Money.of(price.getPrice()));
        }
        
        var updatedProduct = productRepository.save(product);
        
        return CommandResult.success(updatedProduct.getId().getValue());
    }
    
    private Product mustBeExistProduct(ProductId productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Sản phẩm " + productId + " không tồn tại"));
    }
}
