package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.CreateProductCommand;
import com.mts.backend.application.product.command.ProductPriceCommand;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.product.Product;
import com.mts.backend.domain.product.entity.ProductPrice;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.identifier.ProductPriceId;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.repository.ICategoryRepository;
import com.mts.backend.domain.product.repository.IProductRepository;
import com.mts.backend.domain.product.repository.ISizeRepository;
import com.mts.backend.domain.product.value_object.ProductName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
        
        Objects.requireNonNull(command.getName(), "Product name is required");

        
        verifyUniqueName(ProductName.of(command.getName()));
        
        verifyCategoryExists(command.getCategoryId().map(CategoryId::of).orElse(null));
        
        Product product = new Product(
                ProductId.create(),
                ProductName.of(command.getName()),
                command.getDescription().orElse(null),
                command.getImagePath().orElse(null),
                command.getAvailable(),
                command.getSignature(),
                command.getCategoryId().map(CategoryId::of).orElse(null),
                null,
                LocalDateTime.now()
        );
        
        Set<ProductPrice> prices = verifyPriceIfSpecific(product.getId(), command.getProductPrices());
        
        prices.forEach(product::addPrice);
        
        Product result = productRepository.save(product);
        
        return CommandResult.success(result.getId().getValue());
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
    
    private Set<ProductPrice> verifyPriceIfSpecific(ProductId id, Set<ProductPriceCommand> commands){
        Objects.requireNonNull(commands, "Product prices is required");
        Objects.requireNonNull(id, "Product id is required");
        
        Set<ProductPrice> prices = new HashSet<>();
        
        commands.forEach(c -> {
            verifySizeExists(ProductSizeId.of(c.getSizeId()));
            
            ProductPrice price = new ProductPrice(
                    ProductPriceId.create(),
                    id,
                    ProductSizeId.of(c.getSizeId()),
                    Money.of(c.getPrice()), 
                    LocalDateTime.now()
            );

            prices.add(price);
        });
        
        return prices;
    }
    
    private void verifySizeExists(ProductSizeId sizeId) {
        if (!sizeRepository.existsById(sizeId)) {
            throw new NotFoundException("Size " + sizeId.getValue() + " không tồn tại");
        }
    }
    
}