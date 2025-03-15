package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.UpdateProductInformCommand;
import com.mts.backend.domain.product.Product;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.repository.ICategoryRepository;
import com.mts.backend.domain.product.repository.IProductRepository;
import com.mts.backend.domain.product.value_object.ProductName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateProductCommandHandler implements ICommandHandler<UpdateProductInformCommand, CommandResult> {
    private final IProductRepository productRepository;
    private final ICategoryRepository categoryRepository;
    
    public UpdateProductCommandHandler(IProductRepository productRepository, ICategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    public CommandResult handle(UpdateProductInformCommand command) {
        Objects.requireNonNull(command.getProductId(), "Product id is required");
        
        var product = mustBeExistProduct(ProductId.of(command.getProductId()));
        
        product.changeAvailable(command.isAvailable());
        product.changeSignature(command.isSignature());
        product.changeDescription(command.getDescription());
        product.changeImagePath(command.getImagePath());
        product.changeCategory(CategoryId.of(command.getCategoryId()));
        
        if (product.changeName(ProductName.of(command.getName()))) {
            verifyUniqueName(product.getName());
        }
        
        var updatedProduct = productRepository.save(product);
        
        return CommandResult.success(updatedProduct.getId().getValue());
    }
    
    private Product mustBeExistProduct(ProductId productId) {
        return productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Sản phẩm " + productId + " không tồn tại"));
    }
    
    private void verifyUniqueName(ProductName name) {
        Objects.requireNonNull(name, "Product name is required");
        
        productRepository.findByName(name).ifPresent(p -> {
            throw new DuplicateException("Product " + name.getValue() + " đã tồn tại");
        });
    }
    
    private void verifyCategoryExists(CategoryId categoryId) {
        if (categoryId != null && !categoryRepository.existsById(categoryId)) {
            throw new DuplicateException("Category " + categoryId.getValue() + " không tồn tại");
        }
    }
}

