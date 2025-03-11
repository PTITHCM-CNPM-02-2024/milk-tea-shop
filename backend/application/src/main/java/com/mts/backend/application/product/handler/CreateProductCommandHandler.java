package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.CreateProductCommand;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.Product;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.entity.ProductPrice;
import com.mts.backend.domain.product.identifier.ProductPriceId;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.repository.ICategoryRepository;
import com.mts.backend.domain.product.repository.IProductRepository;
import com.mts.backend.domain.product.repository.ISizeRepository;
import com.mts.backend.domain.product.value_object.ProductName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class 
CreateProductCommandHandler implements ICommandHandler<CreateProductCommand, CommandResult> {
    private final IProductRepository productRepository;
    private final ICategoryRepository categoryRepository;
    private final ISizeRepository sizeRepository;
    public CreateProductCommandHandler(IProductRepository productRepository, ICategoryRepository categoryRepository, ISizeRepository sizeRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.sizeRepository = sizeRepository;
    }

    @Override
    public CommandResult handle(CreateProductCommand command) {
        
        try {
            Product product = new Product(
                    ProductId.create(),
                    ProductName.of(command.getName()),
                    command.getDescription().orElse(null),
                    command.getImagePath().orElse(null),
                    command.isAvailable(),
                    command.isSignature(),
                    command.getCategoryId().map(CategoryId::of).orElse(null),
                    null,
                    command.getCreatedAt().orElse(null),
                    command.getUpdatedAt().orElse(null)
            );
            
            if (command.getPrices().isPresent()) {
                Map<Integer, BigDecimal> prices = command.getPrices().get();
                Set<ProductPrice> productPrices = new HashSet<>();
                for (Map.Entry<Integer, BigDecimal> entry : prices.entrySet()) {
                    ProductSizeId productSizeId = ProductSizeId.of(entry.getKey());
                    ProductPrice productPrice = new ProductPrice(
                            ProductPriceId.create(),
                            product.getId(),
                            productSizeId,
                            Money.of(entry.getValue()),
                            command.getCreatedAt().orElse(null),
                            command.getUpdatedAt().orElse(null)
                    );
                    productPrices.add(productPrice);
                }
                
                productPrices.forEach(product::addPrice);
            }
            
            Product savedProduct = productRepository.create(product);
            
            return CommandResult.success(savedProduct.getId().getValue());
        } catch (Exception e) {
            return CommandResult.businessFail(e.getMessage());
        }
    }
}