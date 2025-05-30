package com.mts.backend.application.order.handler;

import com.mts.backend.application.order.command.CreateOrderCommand;
import com.mts.backend.application.order.command.OrderDiscountCommand;
import com.mts.backend.application.order.command.OrderProductCommand;
import com.mts.backend.application.order.command.OrderTableCommand;
import com.mts.backend.application.order.response.OrderBasicResponse;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.customer.CustomerEntity;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.jpa.JpaCustomerRepository;
import com.mts.backend.domain.customer.jpa.JpaMembershipTypeRepository;
import com.mts.backend.domain.customer.value_object.RewardPoint;
import com.mts.backend.domain.order.OrderEntity;
import com.mts.backend.domain.order.OrderProductEntity;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.jpa.JpaOrderRepository;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.product.ProductEntity;
import com.mts.backend.domain.product.ProductPriceEntity;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.jpa.JpaProductPriceRepository;
import com.mts.backend.domain.product.jpa.JpaProductRepository;
import com.mts.backend.domain.promotion.DiscountEntity;
import com.mts.backend.domain.promotion.jpa.JpaDiscountRepository;
import com.mts.backend.domain.staff.EmployeeEntity;
import com.mts.backend.domain.staff.identifier.EmployeeId;
import com.mts.backend.domain.staff.jpa.JpaEmployeeRepository;
import com.mts.backend.domain.store.jpa.JpaServiceTableRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CreateOrderCommandHandler implements ICommandHandler<CreateOrderCommand, CommandResult> {

    private final JpaOrderRepository orderRepository;
    private final JpaEmployeeRepository employeeRepository;
    private final JpaProductRepository productRepository;
    private final JpaDiscountRepository discountRepository;
    private final JpaMembershipTypeRepository membershipTypeRepository;
    private final JpaCustomerRepository customerRepository;
    private final JpaServiceTableRepository serviceTableRepository;
    private final JpaProductPriceRepository productPriceRepository;
    private final static Integer POINT = 1;
    public CreateOrderCommandHandler(
            JpaOrderRepository orderRepository,
            JpaEmployeeRepository employeeRepository,
            JpaProductRepository productRepository,
            JpaDiscountRepository discountRepository,
            JpaMembershipTypeRepository membershipTypeRepository,
            JpaCustomerRepository customerRepository,
            JpaServiceTableRepository serviceTableRepository,
            JpaProductPriceRepository productPriceRepository) {
        this.orderRepository = orderRepository;
        this.employeeRepository = employeeRepository;
        this.productRepository = productRepository;
        this.discountRepository = discountRepository;
        this.membershipTypeRepository = membershipTypeRepository;
        this.customerRepository = customerRepository;
        this.serviceTableRepository = serviceTableRepository;
        this.productPriceRepository = productPriceRepository;
    }

    /**
     * @param command
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(CreateOrderCommand command) {
        Objects.requireNonNull(command, "Create order command is required");
        
        if (command.getOrderProducts() == null || command.getOrderProducts().isEmpty()) {
            throw new DomainException("Đơn hàng phải có ít nhất một sản phẩm");
        }
        
        OrderEntity order = createInitialOrder(command);
        
        addProductsToOrder(order, command.getOrderProducts());
        addMemberDiscountValue(order, command.getCustomerId().orElse(null));
        addDiscountsToOrder(order, command.getOrderDiscounts());
        addTablesToOrder(order, command.getOrderTables());
        
        order.setPoint(Long.valueOf(POINT));
        
        var saveOrder = orderRepository.save(order);

        OrderBasicResponse response = 
                OrderBasicResponse.builder().customerId(saveOrder.getCustomerEntity().map(CustomerEntity::getId).orElse(null))
                        .employeeId(saveOrder.getEmployeeEntity().getId())
                        .finalAmount(saveOrder.getFinalAmount().map(Money::getValue).orElse(null))
                        .note(saveOrder.getCustomizeNote().orElse(null))
                        .orderId(saveOrder.getId())
                        .orderStatus(saveOrder.getStatus().map(Enum::name).orElse(null))
                        .orderTime(saveOrder.getOrderTime())
                        .totalAmount(saveOrder.getTotalAmount().map(Money::getValue).orElse(null))
                        .build();
        
        return CommandResult.success(response);
    }

    private OrderEntity createInitialOrder(CreateOrderCommand command) {
        OrderId orderId = OrderId.create();
        EmployeeEntity employee = mustExistEmployee(command.getEmployeeId());
        return OrderEntity.builder()
                .id(orderId.getValue())
                .employeeEntity(employee)
                .orderTime(Instant.now())
                .customizeNote(command.getNote())
                .status(OrderStatus.PROCESSING)
                .orderProducts(new HashSet<>())
                .orderDiscounts(new HashSet<>())
                .orderTables(new HashSet<>())
                .build();
    }

    
    private EmployeeEntity mustExistEmployee(EmployeeId employeeId) {
        Objects.requireNonNull(employeeId, "Employee id is required");
        
        var employee = employeeRepository.findById(employeeId.getValue())
                .orElseThrow(() -> new DomainException("Nhân viên không tồn tại"));
        
        
        if (employee.getAccountEntity().getActive().isPresent() && !employee.getAccountEntity().getActive().get()){
            throw new DomainException("Tài khoản nhân viên không hoạt động");
        }
        
        if (employee.getAccountEntity().getLocked()){
            throw new DomainException("Tài khoản nhân viên đã bị khóa");
        }
        
        return employee;
    }

    private void addProductsToOrder(OrderEntity order, List<OrderProductCommand> orderProductCommands) {
        Objects.requireNonNull(orderProductCommands, "Order product command is required");
        
        Set<ProductPriceEntity> productPrices = new HashSet<>();
        for (OrderProductCommand orderProductCommand : orderProductCommands) {
            var price = productPriceRepository.findByProductEntity_IdAndSize_Id(orderProductCommand.getProductId().getValue(),
                    orderProductCommand.getSizeId().getValue())
                    .orElseThrow(() -> new DomainException("Đơn hàng không thể tạo do sản phẩm không tồn tại"));
            
            if (!price.getProductEntity().isOrdered()){
                throw new DomainException("Sản phẩm không thể đặt hàng");
            }
            
            order.addOrderProduct(price, orderProductCommand.getOption(), orderProductCommand.getQuantity());
        }
        
    }
    
     private void addDiscountsToOrder(OrderEntity order, List<OrderDiscountCommand> orderDiscountCommands) {
        
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
    
   private void addMemberDiscountValue(OrderEntity order, CustomerId customerId){
        
        if (customerId == null){
            return;
        }
        var customer = customerRepository.findById(customerId.getValue())
                .orElseThrow(() -> new DomainException("Khách hàng không tồn tại"));
        
        order.setCustomerEntity(customer);
        
        customer.increaseRewardPoint(RewardPoint.builder()
                .value(POINT)
                .build());
        
    }
    
    private void addTablesToOrder(OrderEntity order, List<OrderTableCommand> tables){
        
        for (OrderTableCommand tbl : tables) {
            var table = serviceTableRepository.findById(tbl.getServiceTableId().getValue())
                    .orElseThrow(() -> new DomainException("Đơn hàng không thể tạo do bàn không tồn tại"));
            
            if(!table.getActive()){
                throw new DomainException("Bàn %s không hoạt động".formatted(table.getTableNumber().getValue()));
            }
            order.addOrderTable(table);
        }
        
    }
    
    private void checkDiscountToApply(OrderEntity order, DiscountEntity discount){

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
                .mapToInt(OrderProductEntity::getQuantity)
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
        if (order.getCustomerEntity().isPresent() && discount.getMaxUsesPerCustomer().isPresent()) {
            var customerId = order.getCustomerEntity().get().getId();
            var currentDiscountUseByCustomer = orderRepository.countByCustomerEntity_IdAndOrderDiscounts_Discount_IdAndStatus(
                    customerId, discount.getId(), OrderStatus.COMPLETED);

            if (currentDiscountUseByCustomer >= discount.getMaxUsesPerCustomer().get()) {
                throw new DomainException("Khuyến mãi đã hết lượt sử dụng cho khách hàng");
            }
        }
    }

}
