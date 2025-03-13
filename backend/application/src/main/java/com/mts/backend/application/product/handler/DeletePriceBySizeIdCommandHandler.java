package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.DeletePriceBySizeIdCommand;
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
public class DeletePriceBySizeIdCommandHandler implements ICommandHandler<DeletePriceBySizeIdCommand, CommandResult> {
    
    private final IProductRepository productRepository;
    
    public DeletePriceBySizeIdCommandHandler(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public CommandResult handle(DeletePriceBySizeIdCommand command) {
        Objects.requireNonNull(command, "DeletePriceBySizeIdCommand is required");
        
        var product = mustBeExistProduct(ProductId.of(command.getProductId()));
        
        product.delete(ProductSizeId.of(command.getSizeId()));
        
        var updatedProduct = productRepository.save(product);
        
        return CommandResult.success(updatedProduct.getId().getValue());
    }
    
    private Product mustBeExistProduct(ProductId productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Sản phẩm " + productId + " không tồn tại"));
    }
    
    
}
