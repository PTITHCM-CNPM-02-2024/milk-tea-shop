package com.mts.backend.application.order.handler;

import com.mts.backend.application.order.command.CalculateOrderCommand;
import com.mts.backend.application.order.command.OrderDiscountCommand;
import com.mts.backend.application.order.command.OrderProductCommand;
import com.mts.backend.application.order.response.OrderDetailResponse;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.jpa.JpaCustomerRepository;
import com.mts.backend.domain.order.Order;
import com.mts.backend.domain.order.OrderProduct;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.jpa.JpaOrderRepository;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.product.ProductPrice;
import com.mts.backend.domain.product.jpa.JpaProductPriceRepository;
import com.mts.backend.domain.product.jpa.JpaProductRepository;
import com.mts.backend.domain.promotion.Discount;
import com.mts.backend.domain.promotion.jpa.JpaDiscountRepository;
import com.mts.backend.domain.staff.Employee;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class CalculateOrderCommandHandler implements ICommandHandler<CalculateOrderCommand, CommandResult> {
    private final JpaProductRepository productRepository;
    private final JpaProductPriceRepository productPriceRepository;
    private final JpaDiscountRepository discountRepository;
    private final JpaOrderRepository orderRepository;
    private final JpaCustomerRepository customerRepository;
    public CalculateOrderCommandHandler(JpaProductRepository productRepository,
                                        JpaProductPriceRepository productPriceRepository, JpaDiscountRepository discountRepository,
                                        JpaOrderRepository orderRepository, JpaCustomerRepository customerRepository) {
        this.productRepository = productRepository;
        this.productPriceRepository = productPriceRepository;
        this.discountRepository = discountRepository;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * @param command 
     * @return
     */
    @Override
    public CommandResult handle(CalculateOrderCommand command) {
        Objects.requireNonNull(command, "CalculateOrderCommand must not be null");
        
        var order = Order.builder()
                .id(OrderId.create().getValue())
                .employee(Employee.builder()
                        .id(command.getEmployeeId().getValue())
                        .build())
                .status(OrderStatus.PROCESSING)
                .build();

        addProductsToOrder(order, command.getOrderProducts());
        addCustomerToOrder(order, command);
        addDiscountsToOrder(order, command.getOrderDiscounts());
        
        var response = OrderDetailResponse.builder()
                .orderId(order.getId())
                .employeeId(command.getEmployeeId().getValue())
                .customerId(command.getCustomerId().map(CustomerId::getValue).orElse(null))
                .finalAmount(order.getFinalAmount().map(v -> v.getValue()).orElse(null))
                .totalAmount(order.getTotalAmount().map(v -> v.getValue()).orElse(null))
                .discountAmount(order.getDiscountAmount().map(v -> v.getValue()).orElse(null))
                .build();
        
        return CommandResult.success(response);
        
    }
    
    private void addCustomerToOrder(Order order, CalculateOrderCommand command) {

        if (command.getCustomerId().isPresent()) {
            var customer = customerRepository.findByIdFetchMembershipType(command.getCustomerId().get().getValue())
                    .orElseThrow(() -> new DomainException("Khách hàng không tồn tại"));

            order.setCustomer(customer);
        }
    }
    
    
    private void addProductsToOrder(Order order, List<OrderProductCommand> orderProductCommands) {
        Objects.requireNonNull(orderProductCommands, "Order product command is required");

        Set<ProductPrice> productPrices = new HashSet<>();
        for (OrderProductCommand orderProductCommand : orderProductCommands) {
            var price =
                    productPriceRepository.findByProductEntity_IdAndSize_IdFetchPrices(orderProductCommand.getProductId().getValue(),
                            orderProductCommand.getSizeId().getValue())
                    .orElseThrow(() -> new DomainException("Đơn hàng không thể tạo do sản phẩm không tồn tại"));

            if (!price.getProduct().isOrdered()){
                throw new DomainException("Sản phẩm không thể đặt hàng");
            }

            order.addProduct(price, orderProductCommand.getOption(), orderProductCommand.getQuantity());
        }

    }


    private void addDiscountsToOrder(Order order, List<OrderDiscountCommand> orderDiscountCommands) {
        
        for (OrderDiscountCommand orderDiscountCommand : orderDiscountCommands) {
            var discount = discountRepository.findById(orderDiscountCommand.getDiscountId().getValue())
                    .orElseThrow(() -> new DomainException("Đơn hàng không thể tạo do mã giảm giá không tồn tại"));
            
            checkDiscountToApply(order, discount);

            try {
                // Thêm discount vào order
                order.addDiscount(discount);
            } catch (Exception e) {
                throw new DomainException("Không thể áp dụng mã giảm giá: " + e.getMessage());
            }            
        }
        
        
    }

    private void checkDiscountToApply(Order order, Discount discount){

        if (!discount.isApplicable()) {
            String baseError = "Khuyến mãi không thể áp dụng: ";

            if (!discount.getActive()) {
                throw new DomainException(baseError + "Chương trình giảm giá không còn hiệu lực");
            }

            if (!discount.isInValidTimeRange()) {
                LocalDateTime now = LocalDateTime.now();
                if (discount.getValidFrom().isPresent() && now.isBefore(discount.getValidFrom().get())) {
                    throw new DomainException(baseError + "Chương trình giảm giá chưa có hiệu lực. Chỉ bắt đầu từ " +
                            discount.getValidFrom().get());
                } else {
                    throw new DomainException(baseError + "Chương trình giảm giá đã hết hạn. Chỉ áp dụng đến " +
                            discount.getValidUntil());
                }
            }

            if (!discount.hasRemainingUses()) {
                throw new DomainException(baseError + "Chương trình giảm giá đã hết lượt sử dụng");
            }
        }

        // Kiểm tra điều kiện liên quan đến đơn hàng

        // 1. Kiểm tra số lượng sản phẩm tối thiểu
        int totalQuantity = order.getOrderProducts().stream()
                .mapToInt(OrderProduct::getQuantity)
                .sum();

        if (discount.getMinRequiredProduct().isPresent() && totalQuantity < discount.getMinRequiredProduct().get()) {
            throw new DomainException("Khuyến mãi chỉ áp dụng cho đơn hàng có số lượng sản phẩm từ " +
                    discount.getMinRequiredProduct().get());
        }

        // 2. Kiểm tra giá trị đơn hàng tối thiểu
        if (order.getTotalAmount().isPresent() &&
                discount.getMinRequiredOrderValue().compareTo(order.getTotalAmount().get()) > 0) {
            throw new DomainException("Khuyến mãi chỉ áp dụng cho đơn hàng có giá trị từ " +
                    discount.getMinRequiredOrderValue().getValue());
        }

        // 3. Kiểm tra số lần sử dụng của khách hàng
        if (order.getCustomer().isPresent() && discount.getMaxUsesPerCustomer().isPresent()) {
            var customerId = order.getCustomer().get().getId();
            var currentDiscountUseByCustomer = orderRepository.countByCustomerEntity_IdAndOrderDiscounts_Discount_IdAndStatus(
                    customerId, discount.getId(), OrderStatus.COMPLETED);

            if (currentDiscountUseByCustomer >= discount.getMaxUsesPerCustomer().get()) {
                throw new DomainException("Khuyến mãi đã hết lượt sử dụng cho khách hàng");
            }
        }
    }

}
