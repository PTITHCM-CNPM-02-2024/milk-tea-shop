package com.mts.backend.application.order.handler;

import com.mts.backend.application.order.command.CreateOrderCommand;
import com.mts.backend.application.order.command.OrderDiscountCommand;
import com.mts.backend.application.order.command.OrderProductCommand;
import com.mts.backend.application.order.command.OrderTableCommand;
import com.mts.backend.application.order.response.OrderBasicResponse;
import com.mts.backend.domain.common.value_object.MemberDiscountValue;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.repository.ICustomerRepository;
import com.mts.backend.domain.customer.repository.IMembershipTypeRepository;
import com.mts.backend.domain.order.Order;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.repository.IOrderRepository;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.product.Product;
import com.mts.backend.domain.product.entity.ProductPrice;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.identifier.ProductPriceId;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.repository.IProductRepository;
import com.mts.backend.domain.promotion.Discount;
import com.mts.backend.domain.promotion.identifier.DiscountId;
import com.mts.backend.domain.promotion.repository.IDiscountRepository;
import com.mts.backend.domain.staff.identifier.EmployeeId;
import com.mts.backend.domain.staff.repository.IEmployeeRepository;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.domain.store.repository.IServiceTableRepository;
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

    private final IOrderRepository orderRepository;
    private final IEmployeeRepository employeeRepository;
    private final IProductRepository productRepository;
    private final IDiscountRepository discountRepository;
    private final IMembershipTypeRepository membershipTypeRepository;
    private final ICustomerRepository customerRepository;
    private final IServiceTableRepository serviceTableRepository;
    public CreateOrderCommandHandler(IOrderRepository orderRepository, IDiscountRepository discountRepository,
                                     IServiceTableRepository serviceTableRepository, ICustomerRepository customerRepository, IEmployeeRepository employeeRepository, IProductRepository productRepository, 
                                        IMembershipTypeRepository membershipTypeRepository, ICustomerRepository customerRe) {
        this.orderRepository = orderRepository;
        this.employeeRepository = employeeRepository;
        this.productRepository = productRepository;
        this.discountRepository = discountRepository;
        this.membershipTypeRepository = membershipTypeRepository;
        this.serviceTableRepository = serviceTableRepository;
        this.customerRepository = customerRe;
        
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
        
        Order order = createInitialOrder(command);
        
        addProductsToOrder(order, command.getOrderProducts());
        addDiscountsToOrder(order, command.getOrderDiscounts());
        addTablesToOrder(order, command.getOrderTables());
        
        var saveOrder = orderRepository.save(order);

        OrderBasicResponse response = 
                OrderBasicResponse.builder().customerId(saveOrder.getCustomerId().isPresent() ?
                        saveOrder.getCustomerId().get().getValue() : null)
                        .employeeId(saveOrder.getEmployeeId().getValue())
                        .finalAmount(saveOrder.getFinalAmount().map(Money::getAmount).orElse(null))
                        .note(saveOrder.getCustomizeNote().orElse(null))
                        .orderId(saveOrder.getId().getValue())
                        .orderStatus(saveOrder.getStatus().map(OrderStatus::name).orElse(null))
                        .orderTime(saveOrder.getOrderTime())
                        .totalAmount(saveOrder.getTotalAmount().map(Money::getAmount).orElse(null))
                        .build();
        
        return CommandResult.success(response);
    }

    @Transactional
    protected Order createInitialOrder(CreateOrderCommand command) {
        OrderId orderId = OrderId.create();
        EmployeeId employeeId = mustExistEmployee(EmployeeId.of(command.getEmployeeId()));
        Optional<CustomerId> customerId = command.getCustomerId() != null ? Optional.of(CustomerId.of(command.getCustomerId())) : Optional.empty();
        return new Order(
                orderId,
                customerId.orElse(null),
                employeeId,
                Instant.now(),
                Money.ZERO,
                Money.ZERO,
                OrderStatus.PROCESSING,
                command.getNote(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                addMemberDiscountValue(customerId.orElse(null)).orElse(null),
                LocalDateTime.now()
        );
    }

    @Transactional
    protected EmployeeId mustExistEmployee(EmployeeId employeeId) {
        return employeeRepository.findById(employeeId).orElseThrow(() -> new DomainException("Đơn hàng không thể tạo " +
                "do nhân viên không tồn tại")).getId();
    }
    
    @Transactional
    protected Product mustExistProductAndIsOrdered(ProductId productId) {
        var prod = productRepository.findById(productId).orElseThrow(() -> new DomainException("Đơn hàng không thể " +
                "tạo " +
                "do sản phẩm không tồn tại"));
        
        if (!prod.isOrdered()){
            throw new DomainException("Sản phẩm không thể đặt hàng");
        }
        
        return prod;
    }

    @Transactional
    protected void addProductsToOrder(Order order, List<OrderProductCommand> orderProductCommands) {
        
        for (OrderProductCommand orderProductCommand : orderProductCommands) {
            Product product = mustExistProductAndIsOrdered(ProductId.of(orderProductCommand.getProductId()));

            ProductPrice priceId = product.getPrice(ProductSizeId.of(orderProductCommand.getSizeId()))
                    .orElseThrow(() -> new DomainException("Đơn hàng không thể tạo do kích thước sản phẩm không tồn tại"));
            
            order.addProduct(priceId.getId(), orderProductCommand.getQuantity(), orderProductCommand.getOption(),
                    priceId.getPrice());
            
            
        }
    }
    
    @Transactional protected void addDiscountsToOrder(Order order, List<OrderDiscountCommand> orderDiscountCommands) {
        
        for (OrderDiscountCommand orderDiscountCommand : orderDiscountCommands) {
            var discount = discountRepository.findById(DiscountId.of(orderDiscountCommand.getDiscountId()))
                    .orElseThrow(() -> new DomainException("Đơn hàng không thể tạo do mã giảm giá không tồn tại"));
            
            checkDiscountToApply(order, discount);
            
            order.addDiscount(discount.getId(), discount.getDiscountValue());
            
            discount.increaseCurrentUsage();
            
            discountRepository.save(discount);
        }
        
        
    }
    
   @Transactional protected Optional<MemberDiscountValue> addMemberDiscountValue(CustomerId customerId){
        
        if (customerId == null){
            return Optional.empty();
        }
        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new DomainException("Khách hàng không tồn tại"));
        
        var membershipType = membershipTypeRepository.findById(customer.getMembershipTypeId())
                .orElseThrow(() -> new DomainException("Loại thành viên không tồn tại"));
        
        var today = LocalDateTime.now();
        
        if (today.isAfter(membershipType.getValidUntil())){
            return Optional.empty();
        }
        
        return Optional.of(membershipType.getDiscountValue());
    }
    
    private void addTablesToOrder(Order order, List<OrderTableCommand> tables){
        
        for (OrderTableCommand tbl : tables) {
            var table = serviceTableRepository.findById(ServiceTableId.of(tbl.getServiceTableId()))
                    .orElseThrow(() -> new DomainException("Đơn hàng không thể tạo do bàn không tồn tại"));
            
            order.addTable(table.getId());
        }
        
    }
    
    private void checkDiscountToApply(Order order, Discount discount){
        
        var today = LocalDateTime.now();
        
        List<String> errors = new ArrayList<>();
        
        if (!discount.isActive()){
            errors.add("Chương trình giảm giá không còn hiệu lực");
        }
        
        if (discount.getValidFrom().isPresent()){
            if (today.isBefore(discount.getValidFrom().get())){
                errors.add("Chương trình giảm giá" + discount.getName().getValue() + "chưa có hiệu lực. Chỉ bắt đầu " +
                        "từ " + discount.getValidFrom().get());
            }
        }
        
        if (today.isAfter(discount.getValidUntil())){
            errors.add("Chương trình giảm giá" + discount.getName().getValue() + "đã hết hạn. Chỉ áp dụng đến " +
                    discount.getValidUntil());
        }
        
        if (discount.getMinRequiredProduct().isPresent() && order.getOrderProducts().stream().reduce(0, (subtotal, item) -> subtotal + item.getQuantity(), Integer::sum) < discount.getMinRequiredProduct().get()){
            errors.add("Chương trình giảm giá" + discount.getName().getValue() + "chỉ áp dụng cho đơn hàng có số lượng " +
                    "sản phẩm từ " + discount.getMinRequiredProduct().get());
        }
        
        if (order.getTotalAmount().isPresent() && discount.getMinRequiredOrderValue().compareTo(order.getTotalAmount().get()) > 0){
            errors.add("Chương trình giảm giá" + discount.getName().getValue() + "chỉ áp dụng cho đơn hàng có giá trị " +
                    "từ " + discount.getMinRequiredOrderValue());
        }
        
        if (order.getCustomerId().isPresent() && discount.getMaxUsagePerCustomer().isPresent()){
            var currentDiscountUseByCustomer = orderRepository.countOrderAndApplyDiscount(order.getCustomerId().get(),
                    discount.getId());
            
            if (currentDiscountUseByCustomer >= discount.getMaxUsagePerCustomer().get()){
                errors.add("Chương trình giảm giá" + discount.getName().getValue() + "đã hết lượt sử dụng" +
                        " cho khách hàng");
            }
            
        }
        
        if (errors.isEmpty()){
            return;
        }
        
        throw new DomainException("Chương trình giảm giá không thể áp dụng: " + errors);
    }

}
