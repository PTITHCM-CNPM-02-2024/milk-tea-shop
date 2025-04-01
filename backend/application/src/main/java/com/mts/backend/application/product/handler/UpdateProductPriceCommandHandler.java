package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.UpdateProductPriceCommand;
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
        
        var product = mustBeExistProduct(command.getProductId());
        
        for (var price : command.getProductPrices()){
            product.changeProductPrice(price.getSizeId(), price.getPrice());
        }
        
        var updatedProduct = productRepository.save(product);
        
        return CommandResult.success(updatedProduct.getId());
    }
    
    private ProductEntity mustBeExistProduct(ProductId productId) {
        return productRepository.findById(productId.getValue())
                .orElseThrow(() -> new NotFoundException("Sản phẩm " + productId + " không tồn tại"));
    }
}
