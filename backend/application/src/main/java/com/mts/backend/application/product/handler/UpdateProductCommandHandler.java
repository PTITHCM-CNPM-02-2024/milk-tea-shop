package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.UpdateProductInformCommand;
import com.mts.backend.domain.product.Category;
import com.mts.backend.domain.product.Product;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.jpa.JpaCategoryRepository;
import com.mts.backend.domain.product.jpa.JpaProductRepository;

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
public class UpdateProductCommandHandler implements ICommandHandler<UpdateProductInformCommand, CommandResult> {
    private final JpaProductRepository productRepository;
    private final JpaCategoryRepository categoryRepository;

    public UpdateProductCommandHandler(JpaProductRepository productRepository,
            JpaCategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * @param command
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(UpdateProductInformCommand command) {
        Objects.requireNonNull(command.getProductId(), "Product id is required");

        try {
            var product = mustBeExistProduct(command.getProductId());

            product.setAvailable(command.isAvailable());
            product.setSignature(command.isSignature());
            product.setDescription(command.getDescription());
            product.setImagePath(command.getImagePath());
            product.setCategory(
                    command.getCategoryId().map(c -> categoryRepository.getReferenceById(c.getValue())).orElse(null));
            product.setName(command.getName());


            return CommandResult.success(product.getId());
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_product_name")) {
                throw new DuplicateException("Tên sản phẩm " + command.getName().getValue() + " đã tồn tại");
            }
            if (e.getMessage().contains("fk_product_category")) {
                throw new NotFoundException(
                        "Danh mục " + command.getCategoryId().orElse(null).getValue() + " không tồn tại");
            }
            throw new DomainException("Lỗi khi cập nhật sản phẩm", e);
        }
    }

    private Product mustBeExistProduct(ProductId productId) {
        return productRepository.findById(productId.getValue())
                .orElseThrow(() -> new NotFoundException("Sản phẩm " + productId + " không tồn tại"));
    }
}
