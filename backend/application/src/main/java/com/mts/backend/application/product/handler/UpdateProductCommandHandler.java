package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.UpdateProductInformCommand;
import com.mts.backend.domain.product.CategoryEntity;
import com.mts.backend.domain.product.ProductEntity;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.jpa.JpaCategoryRepository;
import com.mts.backend.domain.product.jpa.JpaProductRepository;

import com.mts.backend.domain.product.value_object.ProductName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateProductCommandHandler implements ICommandHandler<UpdateProductInformCommand, CommandResult> {
    private final JpaProductRepository productRepository;
    private final JpaCategoryRepository categoryRepository;
    
    public UpdateProductCommandHandler(JpaProductRepository productRepository, JpaCategoryRepository categoryRepository) {
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
        
        var product = mustBeExistProduct(command.getProductId());
        
        product.setAvailable(command.isAvailable());
        product.setSignature(command.isSignature());
        product.changeDescription(command.getDescription());
        product.changeImagePath(command.getImagePath());
        product.setCategoryEntity(verifyCategoryExists(command.getCategoryId()));
        
        if (product.changeName(command.getName())) {
            verifyUniqueName(command.getProductId(), command.getName());
        }
        
        var updatedProduct = productRepository.save(product);
        
        return CommandResult.success(updatedProduct.getId());
    }
    
    private ProductEntity mustBeExistProduct(ProductId productId) {
        return productRepository.findById(productId.getValue()).orElseThrow(() -> new NotFoundException("Sản phẩm " + productId + " không tồn tại"));
    }
    
    private void verifyUniqueName( ProductId id, ProductName name) {
        Objects.requireNonNull(name, "Product name is required");
        
        if (productRepository.existsByIdNotAndName(id.getValue(), name)) {
            throw new DuplicateException("Tên sản phẩm đã tồn tại");
        }
    }
    
    private CategoryEntity verifyCategoryExists(CategoryId categoryId) {
        if (categoryId == null) {
            return null;
        }
        
        if (!categoryRepository.existsById(categoryId.getValue())){
            throw new NotFoundException("Danh mục " + categoryId + " không tồn tại");
        }
        
        return categoryRepository.getReferenceById(categoryId.getValue());
    }
}

