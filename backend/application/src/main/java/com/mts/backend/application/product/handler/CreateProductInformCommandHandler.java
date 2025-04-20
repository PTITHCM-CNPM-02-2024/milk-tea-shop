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
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

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

        try {

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

            var result = productRepository.save(product);
            return CommandResult.success(result.getId());

        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_product_name")) {
                throw new DuplicateException("Tên sản phẩm đã tồn tại");
            }
            if (e.getMessage().contains("fk_product_category")) {
                throw new NotFoundException("Danh mục sản phẩm không tồn tại");
            }
            if (e.getMessage().contains("fk_product_price_product_size")) {
                throw new NotFoundException("Kích cỡ sản phẩm không tồn tại");
            }

            throw new DomainException("Lỗi khi tạo sản phẩm", e);
        }

    }

}