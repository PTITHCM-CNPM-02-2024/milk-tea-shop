package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.DeletePriceBySizeIdCommand;
import com.mts.backend.domain.order.jpa.JpaOrderRepository;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.product.Product;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.jpa.JpaProductRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class DeletePriceBySizeIdCommandHandler implements ICommandHandler<DeletePriceBySizeIdCommand, CommandResult> {

    private final JpaProductRepository productRepository;
    private final JpaOrderRepository orderRepository;

    public DeletePriceBySizeIdCommandHandler(JpaProductRepository productRepository,
                                             JpaOrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public CommandResult handle(DeletePriceBySizeIdCommand command) {
        Objects.requireNonNull(command, "DeletePriceBySizeIdCommand is required");

        var product = mustBeExistProduct(command.getProductId());
        
        var productPrice = product.getProductPriceEntity(command.getSizeId()).orElseThrow(
                () -> new NotFoundException("Kích thước " + command.getSizeId() + " không tồn tại trong sản phẩm " + command.getProductId()));
        
        var orderCount = orderRepository.countByOrderProducts_ProductPriceEntityAndStatus(
                productPrice, OrderStatus.PROCESSING);
        
        if (orderCount > 0) {
            return CommandResult.businessFail("Không thể xóa kích thước " + command.getSizeId() + " vì nó đang được sử dụng " +
                    "trong đơn hàng");
        }
        
        if (product.removePrice(command.getSizeId())) {
            return CommandResult.success(product.getId());
        }

        return CommandResult.notFoundFail("Không tìm thấy kích thước " + command.getSizeId() + " trong sản phẩm " + command.getProductId());
    }

    private Product mustBeExistProduct(ProductId productId) {
        return productRepository.findById(productId.getValue())
                .orElseThrow(() -> new NotFoundException("Sản phẩm " + productId + " không tồn tại"));
    }
    

}
