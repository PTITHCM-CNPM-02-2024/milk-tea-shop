package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.CreateProductInformCommand;
import com.mts.backend.domain.product.Product;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.repository.ICategoryRepository;
import com.mts.backend.domain.product.repository.IProductRepository;
import com.mts.backend.domain.product.repository.ISizeRepository;
import com.mts.backend.domain.product.value_object.ProductName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class
CreateProductInformCommandHandler implements ICommandHandler<CreateProductInformCommand, CommandResult> {
    private final IProductRepository productRepository;
    private final ICategoryRepository categoryRepository;

    public CreateProductInformCommandHandler(IProductRepository productRepository, ICategoryRepository categoryRepository, ISizeRepository sizeRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CommandResult handle(CreateProductInformCommand command) {
        
        Objects.requireNonNull(command.getName(), "Product name is required");

        
        verifyUniqueName(ProductName.of(command.getName()));
        
        verifyCategoryExists(command.getCategoryId().map(CategoryId::of).orElse(null));

        Product product = new Product(
                ProductId.create(),
                ProductName.of(command.getName()),
                command.getDescription().orElse(""),
                command.getImagePath().orElse(""),
                command.isAvailable(),
                command.isSignature(),
                command.getCategoryId().map(CategoryId::of).orElse(null),
                null,
                command.getCreatedAt().orElse(LocalDateTime.now()),
                command.getUpdatedAt().orElse(LocalDateTime.now())
        );
        
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
    
}