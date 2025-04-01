package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.CreateProductCommand;
import com.mts.backend.domain.product.*;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.jpa.JpaCategoryRepository;
import com.mts.backend.domain.product.jpa.JpaProductPriceRepository;
import com.mts.backend.domain.product.jpa.JpaProductRepository;
import com.mts.backend.domain.product.jpa.JpaProductSizeRepository;
import com.mts.backend.domain.product.value_object.ProductName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class
CreateProductInformCommandHandler implements ICommandHandler<CreateProductCommand, CommandResult> {
    private final JpaCategoryRepository categoryRepository;
    private final JpaProductSizeRepository sizeRepository;
    private final JpaProductRepository productRepository;

    public CreateProductInformCommandHandler(JpaProductPriceRepository priceRepository,
                                             JpaCategoryRepository categoryRepository,
                                             JpaProductSizeRepository sizeRepository, JpaProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.sizeRepository = sizeRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public CommandResult handle(CreateProductCommand command) {

        Objects.requireNonNull(command.getName(), "Product name is required");

        verifyUniqueName(command.getName());

        var categoryEn = verifyCategoryExists(command.getCategoryId().orElse(null));


        var product = ProductEntity.
                builder().
                id(ProductId.create().getValue()).
                name(command.getName()).
                description(command.getDescription().orElse(null)).
                categoryEntity(categoryEn).
                available(command.getAvailable().orElse(true)).
                signature(command.getSignature().orElse(null)).
                build();

        command.getProductPrices().forEach(p -> {
            
            var size =   verifySizeExists(p.getSizeId());
            
            ProductPriceEntity productPrice = ProductPriceEntity.builder()
                    .productEntity(product)
                    .size(size)
                    .price(p.getPrice())
                    .build();
            
            product.addProductPriceEntity(productPrice);

        });
        
        var result = productRepository.save(product);

        return CommandResult.success(result.getId());
    }

    private void verifyUniqueName(ProductName name) {
        Objects.requireNonNull(name, "Product name is required");

        if (productRepository.existsByName(name)) {
            throw new DuplicateException("Tên sản phẩm đã tồn tại");
        }
    }

    private CategoryEntity verifyCategoryExists(CategoryId categoryId) {
        if (categoryId == null) {
            return null;
        }

        if (!categoryRepository.existsById(categoryId.getValue())) {
            throw new NotFoundException("Category " + categoryId.getValue() + " không tồn tại");
        }

        return categoryRepository.getReferenceById(categoryId.getValue());
    }
    
    private ProductSizeEntity verifySizeExists(ProductSizeId sizeId) {
        Objects.requireNonNull(sizeId, "Product size id is required");

        if (!sizeRepository.existsById(sizeId.getValue())) {
            throw new NotFoundException("Size " + sizeId.getValue() + " không tồn tại");
        }
        
        return sizeRepository.getReferenceById(sizeId.getValue());
    }

}