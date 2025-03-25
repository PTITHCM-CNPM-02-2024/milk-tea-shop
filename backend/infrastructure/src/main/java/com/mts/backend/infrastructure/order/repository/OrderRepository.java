package com.mts.backend.infrastructure.order.repository;

import com.mts.backend.domain.common.value_object.MemberDiscountValue;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.customer.MembershipType;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.value_object.MemberTypeName;
import com.mts.backend.domain.order.Order;
import com.mts.backend.domain.order.entity.OrderDiscount;
import com.mts.backend.domain.order.entity.OrderProduct;
import com.mts.backend.domain.order.entity.OrderTable;
import com.mts.backend.domain.order.identifier.OrderDiscountId;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.identifier.OrderProductId;
import com.mts.backend.domain.order.identifier.OrderTableId;
import com.mts.backend.domain.order.repository.IOrderRepository;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.product.identifier.ProductPriceId;
import com.mts.backend.domain.promotion.identifier.DiscountId;
import com.mts.backend.domain.promotion.value_object.PromotionDiscountValue;
import com.mts.backend.domain.staff.identifier.EmployeeId;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.infrastructure.customer.jpa.JpaCustomerRepository;
import com.mts.backend.infrastructure.customer.jpa.JpaMembershipTypeRepository;
import com.mts.backend.infrastructure.order.jpa.JpaOrderDiscountRepository;
import com.mts.backend.infrastructure.order.jpa.JpaOrderProductRepository;
import com.mts.backend.infrastructure.order.jpa.JpaOrderRepository;
import com.mts.backend.infrastructure.order.jpa.JpaOrderTableRepository;
import com.mts.backend.infrastructure.persistence.entity.*;
import com.mts.backend.infrastructure.product.jpa.JpaProductPriceRepository;
import com.mts.backend.infrastructure.promotion.jpa.JpaDiscountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderRepository implements IOrderRepository {

    private final JpaOrderRepository orderRepository;
    private final JpaOrderProductRepository orderProductRepository;
    private final JpaOrderDiscountRepository orderDiscountRepository;
    private final JpaOrderTableRepository orderTableRepository;
    private final JpaProductPriceRepository productPriceRepository;
    private final JpaDiscountRepository discountRepository;
    private final JpaCustomerRepository customerRepository;
    private final JpaMembershipTypeRepository membershipTypeRepository;

    public OrderRepository(JpaOrderRepository orderRepository, JpaOrderProductRepository orderProductRepository,
                           JpaOrderDiscountRepository orderDiscountRepository,
                           JpaOrderTableRepository orderTableRepository,
                           JpaProductPriceRepository productPriceRepository, JpaDiscountRepository discountRepository
            , JpaCustomerRepository customerRepository, JpaMembershipTypeRepository membershipTypeRepository) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.orderDiscountRepository = orderDiscountRepository;
        this.orderTableRepository = orderTableRepository;
        this.productPriceRepository = productPriceRepository;
        this.discountRepository = discountRepository;
        this.customerRepository = customerRepository;
        this.membershipTypeRepository = membershipTypeRepository;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Optional<Order> findById(OrderId id) {
        Objects.requireNonNull(id, "Order id is required");

        Optional<OrderEntity> orderEntity = orderRepository.findById(id.getValue());

        if (orderEntity.isEmpty()) {
            return Optional.empty();
        }

        OrderEntity ordEn = orderEntity.get();

        Set<OrderProduct> products = findProducts(id);
        Set<OrderDiscount> discounts = findDiscounts(id);
        Set<OrderTable> tables = findTables(id);

        MemberDiscountValue memberDiscount = null;
        if (ordEn.getCustomerEntity() != null && ordEn.getCustomerEntity().getId() != null) {
            var memType = findMembershipType(CustomerId.of(ordEn.getCustomerEntity().getId()));
            memberDiscount = memType.map(MembershipType::getDiscountValue).orElse(null);
        }

        return Optional.of(new Order(
                id,
                ordEn.getCustomerEntity() != null && ordEn.getCustomerEntity().getId() != null
                        ? CustomerId.of(ordEn.getCustomerEntity().getId()) : null, 
                EmployeeId.of(ordEn.getEmployeeEntity().getId()),
                ordEn.getOrderTime(),
                Money.of(ordEn.getTotalAmount()),
                Money.of(ordEn.getFinalAmount()),
                ordEn.getStatus(),
                ordEn.getCustomizeNote(),
                products,
                tables,
                discounts,
                memberDiscount,
                ordEn.getUpdatedAt().orElse(LocalDateTime.now())
        ));
    }

    /**
     * @param order
     * @return
     */
    @Override
    @Transactional
    public Order save(Order order) {
        Objects.requireNonNull(order, "Order is required");

        try {
            if (orderRepository.existsById(order.getId().getValue())) {
                return update(order);
            } else {
                return create(order);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while saving order", e);
        }
    }

    @Transactional
    protected Order create(Order order) {
        Objects.requireNonNull(order, "order is required");

        OrderEntity ordEn = OrderEntity.builder()
                .id(order.getId().getValue())
                .totalAmount(order.getTotalAmount().map(Money::getAmount).orElse(null))
                .finalAmount(order.getFinalAmount().map(Money::getAmount).orElse(null))
                .status(order.getStatus().orElse(null))
                .orderTime(order.getOrderTime())
                .build();

        CustomerEntity csEn = CustomerEntity.builder()
                .id(order.getCustomerId().map(CustomerId::getValue).orElse(null))
                .build();
        EmployeeEntity emEn = EmployeeEntity.builder()
                .id(order.getEmployeeId().getValue())
                .build();

        ordEn.setCustomerEntity(csEn);
        ordEn.setEmployeeEntity(emEn);

        orderRepository.insertOrder(ordEn);

        createDiscounts(order.getId(), order.getOrderDiscounts());
        createProducts(order.getId(), order.getOrderProducts());
        createTables(order.getId(), order.getOrderTables());

        return order;

    }


    @Transactional
    protected void createDiscounts(OrderId id, Set<OrderDiscount> discounts) {
        Objects.requireNonNull(id, "Order id is required");
        if (discounts == null || discounts.isEmpty()) {
            return;
        }
        discounts.forEach(discount -> {
            OrderDiscountEntity ordDisEn = OrderDiscountEntity.builder()
                    .id(discount.getId().getValue())
                    .discountAmount(discount.getDiscountAmount().getAmount())
                    .build();

            DiscountEntity disEn = DiscountEntity.builder().id(discount.getDiscountId().getValue()).build();

            OrderEntity ordEn = OrderEntity.builder()
                    .id(id.getValue())
                    .build();

            ordDisEn.setOrderEntity(ordEn);
            ordDisEn.setDiscount(disEn);

            orderDiscountRepository.insertOrderDiscount(ordDisEn);
        });
    }

    @Transactional
    protected void createProducts(OrderId id, Set<OrderProduct> products) {
        Objects.requireNonNull(id, "Order id is required");
        if (products == null || products.isEmpty()) {
            return;
        }
        products.forEach(product -> {
            OrderProductEntity ordProEn = OrderProductEntity.builder()
                    .id(product.getId().getValue())
                    .quantity(product.getQuantity())
                    .build();

            ProductPriceEntity ent = ProductPriceEntity.builder()
                    .id(product.getProductPriceId().getValue())
                    .build();


            OrderEntity ordEn = OrderEntity.builder()
                    .id(id.getValue())
                    .build();
            ordProEn.setOrderEntity(ordEn);
            ordProEn.setProductPriceEntity(ent);

            orderProductRepository.insertOrderProduct(ordProEn);
        });
    }

    @Transactional
    protected void createTables(OrderId id, Set<OrderTable> tables) {
        Objects.requireNonNull(id, "Order id is required");

        tables.forEach(table -> {
            OrderTableEntity ordTabEn = OrderTableEntity.builder()
                    .id(table.getId().getValue())
                    .checkIn(table.getCheckInTime())
                    .checkOut(table.getCheckOutTime().orElse(null))
                    .build();

            ServiceTableEntity serTabEn = ServiceTableEntity.builder()
                    .id(table.getTableId().getValue())
                    .build();

            OrderEntity ordEn = OrderEntity.builder()
                    .id(id.getValue())
                    .build();

            ordTabEn.setOrderEntity(ordEn);
            ordTabEn.setTable(serTabEn);

            orderTableRepository.insertOrderTable(ordTabEn);
        });
    }

    @Transactional
    protected Order update(Order order) {
        Objects.requireNonNull(order, "order is required");

        OrderEntity ordEn = OrderEntity.builder()
                .id(order.getId().getValue())
                .totalAmount(order.getTotalAmount().map(Money::getAmount).orElse(null))
                .finalAmount(order.getFinalAmount().map(Money::getAmount).orElse(null))
                .status(order.getStatus().orElse(null))
                .build();

        CustomerEntity csEn = CustomerEntity.builder()
                .id(order.getCustomerId().map(CustomerId::getValue).orElse(null))
                .build();
        EmployeeEntity emEn = EmployeeEntity.builder()
                .id(order.getEmployeeId().getValue())
                .build();

        ordEn.setCustomerEntity(csEn);
        ordEn.setEmployeeEntity(emEn);

        orderRepository.updateOrder(ordEn);

        updateDiscounts(order.getId(), order.getOrderDiscounts());
        updateProducts(order.getId(), order.getOrderProducts());
        updateTables(order.getId(), order.getOrderTables());

        return order;
    }


    @Transactional
    protected void updateDiscounts(OrderId id, Set<OrderDiscount> discounts) {
        Objects.requireNonNull(id, "Order id is required");

        // Get existing discounts from database
        List<OrderDiscountEntity> existingDiscounts = orderDiscountRepository.findByOrder_Id(id.getValue());

        // Convert to sets for easier comparison
        Set<Long> existingIds = existingDiscounts.stream()
                .map(OrderDiscountEntity::getId)
                .collect(java.util.stream.Collectors.toSet());

        Set<Long> currentIds = discounts.stream()
                .map(d -> d.getId().getValue())
                .collect(java.util.stream.Collectors.toSet());

        // Find discounts to delete (in DB but not in aggregate)
        existingDiscounts.stream()
                .filter(existing -> !currentIds.contains(existing.getId()))
                .forEach(toDelete -> orderDiscountRepository.deleteOrderDiscount(toDelete.getId()));

        // Update or insert discounts
        discounts.forEach(discount -> {
            OrderDiscountEntity ordDisEn = OrderDiscountEntity.builder()
                    .id(discount.getId().getValue())
                    .discountAmount(discount.getDiscountAmount().getAmount())
                    .build();

            DiscountEntity disEn = DiscountEntity.builder()
                    .id(discount.getDiscountId().getValue())
                    .build();

            OrderEntity ordEn = OrderEntity.builder()
                    .id(id.getValue())
                    .build();

            ordDisEn.setOrderEntity(ordEn);
            ordDisEn.setDiscount(disEn);

            if (existingIds.contains(discount.getId().getValue())) {
                orderDiscountRepository.updateOrderDiscount(ordDisEn);
            } else {
                orderDiscountRepository.insertOrderDiscount(ordDisEn);
            }
        });
    }

    @Transactional
    protected void updateProducts(OrderId id, Set<OrderProduct> products) {
        Objects.requireNonNull(id, "Order id is required");

        // Get existing products from DB
        List<OrderProductEntity> existingProducts = orderProductRepository.findByOrderEntity_Id(id.getValue());

        // Identify products to delete (present in DB but not in aggregate)
        existingProducts.forEach(existing -> {
            boolean stillExists = products.stream()
                    .anyMatch(product -> product.getId().equals(OrderProductId.of(existing.getId())));

            if (!stillExists) {
                orderProductRepository.deleteOrderProduct(existing.getId());
            }
        });

        // Update or insert products from the aggregate
        if (products != null) {
            products.forEach(product -> {
                boolean exists = existingProducts.stream()
                        .anyMatch(existing -> existing.getId().equals(product.getId().getValue()));

                OrderProductEntity ordProEn = OrderProductEntity.builder()
                        .id(product.getId().getValue())
                        .quantity(product.getQuantity())
                        .build();

                ProductPriceEntity ent = ProductPriceEntity.builder()
                        .id(product.getProductPriceId().getValue())
                        .build();

                OrderEntity ordEn = OrderEntity.builder()
                        .id(id.getValue())
                        .build();

                ordProEn.setOrderEntity(ordEn);
                ordProEn.setProductPriceEntity(ent);

                if (exists) {
                    orderProductRepository.updateOrderProduct(ordProEn);
                } else {
                    orderProductRepository.insertOrderProduct(ordProEn);
                }
            });
        }
    }

    @Transactional
    protected void updateTables(OrderId id, Set<OrderTable> tables) {
        Objects.requireNonNull(id, "Order id is required");

        // Get existing tables from DB
        List<OrderTableEntity> existingTables = orderTableRepository.findByOrderId(id.getValue());

        // Identify tables to delete (present in DB but not in aggregate)
        existingTables.forEach(existing -> {
            boolean stillExists = tables.stream()
                    .anyMatch(table -> table.getId().equals(OrderTableId.of(existing.getId())));

            if (!stillExists) {
                orderTableRepository.deleteOrderTable(existing.getId());
            }
        });

        // Update or insert tables from the aggregate
        if (tables != null) {
            tables.forEach(table -> {
                boolean exists = existingTables.stream()
                        .anyMatch(existing -> existing.getId().equals(table.getId().getValue()));

                OrderTableEntity ordTabEn = OrderTableEntity.builder()
                        .id(table.getId().getValue())
                        .checkIn(table.getCheckInTime())
                        .checkOut(table.getCheckOutTime().orElse(null))
                        .build();

                ServiceTableEntity serTabEn = ServiceTableEntity.builder()
                        .id(table.getTableId().getValue())
                        .build();

                OrderEntity ordEn = OrderEntity.builder()
                        .id(id.getValue())
                        .build();

                ordTabEn.setOrderEntity(ordEn);
                ordTabEn.setTable(serTabEn);

                if (exists) {
                    orderTableRepository.updateOrderTable(ordTabEn);
                } else {
                    orderTableRepository.insertOrderTable(ordTabEn);
                }
            });
        }
    }

    /**
     * @return
     */
    @Override
    public List<Order> findAll() {
        return List.of();
    }

    public List<Order> findAll(Integer page, Integer size) {
        return List.of();
    }

    /**
     * @param order
     */
    @Override
    public void delete(Order order) {

    }

    /**
     * @param customerId
     * @return
     */
    @Override
    public List<Order> findByCustomerId(CustomerId customerId) {
        return List.of();
    }

    /**
     * @param customerId
     * @param status
     * @return
     */
    @Override
    public List<Order> findByCustomerIdAndStatus(CustomerId customerId, OrderStatus status) {
        return List.of();
    }

    /**
     * @param customerId
     * @param orderId
     * @return
     */
    @Override
    public Optional<Order> findByCustomerIdAndOrderId(CustomerId customerId, OrderId orderId) {
        return Optional.empty();
    }

    /**
     * @param customerId 
     * @return
     */
    @Override
    public Long countOrderAndApplyDiscount(CustomerId customerId, DiscountId discountId) {
        Objects.requireNonNull(customerId, "Customer id is required");
        
        return orderRepository.countCompletedOrdersWithSpecificDiscount(customerId.getValue(), discountId.getValue());
    }


    private Set<OrderProduct> findProducts(OrderId id) {
        Objects.requireNonNull(id, "Order id is required");

        var ordProd = this.orderProductRepository.findByOrderEntity_Id(id.getValue());

        Set<OrderProduct> products = new HashSet<>();

        ordProd.forEach(e -> {
            var prodPrice = productPriceRepository.findById(e.getProductPriceEntity().getId());
            products.add(new OrderProduct(
                    OrderProductId.of(e.getId()),
                    id,
                    ProductPriceId.of(e.getProductPriceEntity().getId()),
                    e.getQuantity(),
                    e.getOption(),
                    Money.of(prodPrice.map(ProductPriceEntity::getPrice).orElse(null)),
                    e.getUpdatedAt().orElse(LocalDateTime.now())
            ));
        });

        return products;
    }

    private Set<OrderDiscount> findDiscounts(OrderId id) {
        Objects.requireNonNull(id, "Order id is required");

        var ordDis = this.orderDiscountRepository.findByOrder_Id(id.getValue());

        Set<OrderDiscount> discounts = new HashSet<>();

        ordDis.forEach(e -> {
            var dis = discountRepository.findById(e.getDiscount().getId());

            PromotionDiscountValue value =
                    dis.map(d -> PromotionDiscountValue.of(d.getDiscountValue(), d.getDiscountUnit(),
                            Money.of(d.getMaxDiscountAmount()))).orElse(null);

            discounts.add(new OrderDiscount(
                    OrderDiscountId.of(e.getId()),
                    id,
                    DiscountId.of(e.getDiscount().getId()),
                    Money.of(e.getDiscountAmount()),
                    value,
                    e.getUpdatedAt().orElse(LocalDateTime.now())
            ));

        });

        return discounts;
    }

    private Set<OrderTable> findTables(OrderId id) {
        Objects.requireNonNull(id, "Order id is required");

        var ordTab = this.orderTableRepository.findByOrderId(id.getValue());

        Set<OrderTable> tables = new HashSet<>();

        ordTab.forEach(e -> {
            tables.add(new OrderTable(
                    OrderTableId.of(e.getId()),
                    id,
                    ServiceTableId.of(e.getTable().getId()),
                    e.getCheckIn(),
                    e.getCheckOut(),
                    e.getUpdatedAt().orElse(LocalDateTime.now())
            ));
        });

        return tables;
    }

    private Optional<MembershipType> findMembershipType(CustomerId customerId) {

        var cus = customerRepository.findById(customerId.getValue());

        return cus.map(e -> {
            var mem = membershipTypeRepository.findById(e.getMembershipTypeEntity().getId());

            return mem.map(m -> new MembershipType(
                    MembershipTypeId.of(m.getId()),
                    MemberTypeName.of(m.getType()),
                    MemberDiscountValue.of(m.getDiscountValue(), m.getDiscountUnit()),
                    m.getRequiredPoint(),
                    m.getDescription(),
                    m.getValidUntil(),
                    m.getIsActive(),
                    m.getUpdatedAt().orElse(LocalDateTime.now())
            )).orElse(null);
        });
    }

    private Order mapToAggregate(OrderEntity entity) {
        // Lấy các collections liên quan
        Set<OrderProduct> orderProducts = findProducts(OrderId.of(entity.getId()));
        Set<OrderTable> orderTables = findTables(OrderId.of(entity.getId()));
        Set<OrderDiscount> orderDiscounts = findDiscounts(OrderId.of(entity.getId()));

        // Lấy giảm giá thành viên nếu có
        MemberDiscountValue memberDiscount = null;
        if (entity.getCustomerEntity() != null && entity.getCustomerEntity().getId() != null) {
            memberDiscount = findMembershipType(CustomerId.of(entity.getCustomerEntity().getId()))
                    .map(MembershipType::getDiscountValue).orElse(null);
        }

        // Tạo Order domain object
        return new Order(
                OrderId.of(entity.getId()),
                entity.getCustomerEntity() != null && entity.getCustomerEntity().getId() != null
                        ? CustomerId.of(entity.getCustomerEntity().getId()) : null,
                EmployeeId.of(entity.getEmployeeEntity().getId()),
                entity.getOrderTime(),
                entity.getTotalAmount() != null ? Money.of(entity.getTotalAmount()) : null,
                entity.getFinalAmount() != null ? Money.of(entity.getFinalAmount()) : null,
                entity.getStatus(),
                entity.getCustomizeNote(),
                orderProducts,
                orderTables,
                orderDiscounts,
                memberDiscount,
                entity.getUpdatedAt().orElse(LocalDateTime.now())
        );
    }

}
