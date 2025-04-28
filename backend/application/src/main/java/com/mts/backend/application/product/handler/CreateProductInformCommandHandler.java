package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.CreateProductCommand;
import com.mts.backend.domain.product.Product;
import com.mts.backend.domain.product.ProductPrice;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.jpa.JpaCategoryRepository;
import com.mts.backend.domain.product.jpa.JpaProductPriceRepository;
import com.mts.backend.domain.product.jpa.JpaProductRepository;
import com.mts.backend.domain.product.jpa.JpaProductSizeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Objects;

@Service
public class CreateProductInformCommandHandler implements ICommandHandler<CreateProductCommand, CommandResult> {
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


        var product = Product.builder().id(ProductId.create().getValue()).name(command.getName())
                .description(command.getDescription().orElse(null))
                .category(command.getCategoryId().map(c -> categoryRepository.getReferenceById(c.getValue()))
                        .orElse(null))
                .available(command.getAvailable().orElse(true)).signature(command.getSignature().orElse(null))
                .imagePath(command.getImagePath().orElse(null)).build();

        command.getProductPrices().forEach(p -> {

            var size = sizeRepository.getReferenceById(p.getSizeId().getValue());

            ProductPrice productPrice = ProductPrice.builder()
                    .product(product)
                    .size(size)
                    .price(p.getPrice())
                    .build();

            product.addPrice(productPrice);

        });
        try {

            var result = productRepository.save(product);
            return CommandResult.success(result.getId());

        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("product.uk_product_name")) {
                return CommandResult.businessFail("Đã tồn tại sản phẩm với tên %s".formatted(command.getName().getValue()));
            }

        }
        
        return CommandResult.businessFail("Lỗi không xác định khi tạo sản phẩm %s".formatted(command.getName().getValue()));
    }

}