package com.mts.backend.domain.order;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.customer.Customer;
import com.mts.backend.domain.order.identifier.OrderDiscountId;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.identifier.OrderProductId;
import com.mts.backend.domain.order.identifier.OrderTableId;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.payment.Payment;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.product.ProductPrice;
import com.mts.backend.domain.product.identifier.ProductPriceId;
import com.mts.backend.domain.promotion.Discount;
import com.mts.backend.domain.staff.Employee;
import com.mts.backend.domain.store.ServiceTable;
import com.mts.backend.shared.exception.DomainException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "`order`", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "employee_id", columnList = "employee_id")
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NamedEntityGraphs(
        {@NamedEntityGraph(name = "graph.order.fetchAll",
                attributeNodes = {
                        @NamedAttributeNode("orderDiscounts"),
                        @NamedAttributeNode("orderProducts"),
                        @NamedAttributeNode("orderTables"),
                        @NamedAttributeNode("customer"),
                        @NamedAttributeNode("employee"),
                        @NamedAttributeNode("payments"),
                }
        ),
                @NamedEntityGraph(name = "graph.order.fetchEmpCus",
                        attributeNodes = {
                                @NamedAttributeNode("employee"),
                                @NamedAttributeNode("customer")
                        }
                )
        }
)
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity<Long> {
    @Id
    @Comment("Mã đơn hàng")
    @Column(name = "order_id", columnDefinition = "int UNSIGNED")
    @NotNull
    @Getter
    private Long id;

    private static Set<OrderDiscount> $default$orderDiscounts() {
        return new LinkedHashSet<>();
    }

    private static Set<OrderProduct> $default$orderProducts() {
        return new LinkedHashSet<>();
    }

    private static Set<OrderTable> $default$orderTables() {
        return new LinkedHashSet<>();
    }

    private static Set<Payment> $default$payments() {
        return new LinkedHashSet<>();
    }

    public static OrderBuilder builder() {
        return new OrderBuilder();
    }

    private boolean setId(@NotNull OrderId id) {
        if (OrderId.of(this.id).equals(id)) {
            return false;
        }
        this.id = id.getValue();
        return true;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã khách hàng")
    @JoinColumn(name = "customer_id")
    @Nullable
    private Customer customer;


    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã nhân viên")
    @JoinColumn(name = "employee_id", nullable = true)
    @NotNull
    private Employee employee;
    
    public boolean setEmployee(@NotNull Employee employee) {
        orderCanBeModified();
        if (Objects.equals(this.employee, employee)) {
            return false;
        }
        this.employee = employee;
        return true;
    }
    
    public Optional<Employee> getEmployee() {
        return Optional.ofNullable(employee);
    }
    
    
    @Comment("Thời gian đặt hàng")
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "order_time", nullable = false)
    @NotNull
    @Getter
    private Instant orderTime;
    
    public boolean setOrderTime(@NotNull Instant orderTime) {
        orderCanBeModified();
        if (Objects.equals(this.orderTime, orderTime)) {
            return false;
        }
        this.orderTime = orderTime;
        return true;
    }
    @Comment("Tổng tiền")
    @Column(name = "total_amount", precision = 11, scale = 3)
    @Nullable
    private BigDecimal totalAmount;
    @Comment("Thành tiền")
    @Column(name = "final_amount", precision = 11, scale = 3)
    @Nullable
    private BigDecimal finalAmount;
    @Comment("Ghi chú tùy chỉnh")
    @Column(name = "customize_note", length = 1000)
    @Nullable
    private String customizeNote;
    @Comment("Trạng thái đơn hàng")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Nullable
    private OrderStatus status;
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    private Set<OrderDiscount> orderDiscounts = new LinkedHashSet<>();
    
    public Set<OrderDiscount> getOrderDiscounts() {
        return Set.copyOf(orderDiscounts);
    }
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    private Set<OrderProduct> orderProducts = new LinkedHashSet<>();
    
    public Set<OrderProduct> getOrderProducts() {
        return Set.copyOf(orderProducts);
    }
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    private Set<OrderTable> orderTables = new LinkedHashSet<>();
    
    public Set<OrderTable> getOrderTables() {
        return Set.copyOf(orderTables);
    }
    @ColumnDefault("'1'")
    @Column(name = "point", columnDefinition = "int UNSIGNED")
    @Nullable
    @Comment("Điểm thưởng")
    @Min(value = 1)
    private Long point;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    private Set<Payment> payments = new LinkedHashSet<>();
    
    public Set<Payment> getPayments() {
        return Set.copyOf(payments);
    }

    public Optional<Customer> getCustomer() {
        return Optional.ofNullable(customer);
    }

    public boolean setCustomer(@NotNull Customer customer) {
        orderCanBeModified();
        if (Objects.equals(this.customer, customer)) {
            return false;
        }
        this.customer = customer;
        recalculateTotalAmount();
        return true;
    }

    public Optional<Money> getTotalAmount() {
        return Optional.ofNullable(totalAmount).map(Money::of);
    }

    public Optional<Money> getFinalAmount() {
        return Optional.ofNullable(finalAmount).map(Money::of);
    }

    public Optional<String> getCustomizeNote() {
        return Optional.ofNullable(customizeNote);
    }

    public Optional<OrderStatus> getStatus() {
        return Optional.ofNullable(status);
    }

    public Optional<Long> getPoint() {
        return Optional.ofNullable(point);
    }

    public void setPoint(Long point) {
        orderCanBeModified();
        this.point = point;
    }

    public OrderDiscount addDiscount(Discount discount) {
        orderCanBeModified();
        var existingOrderDiscount = findDiscount(discount);

        if (existingOrderDiscount.isPresent()) {
            return existingOrderDiscount.get();
        }

        var orderDiscount = OrderDiscount.builder()
                .discountAmount(Money.ZERO)
                .id(OrderDiscountId.create().getValue())
                .discount(discount)
                .order(this)
                .build();
        discount.increaseCurrentUses();
        orderDiscounts.add(orderDiscount);
        recalculateTotalAmount();
        return orderDiscount;
    }

    public boolean removeDiscount(Discount discount) {
        orderCanBeModified();
        var existingOrderDiscount = findDiscount(discount);

        if (existingOrderDiscount.isPresent()) {
            orderDiscounts.remove(existingOrderDiscount.get());
            recalculateTotalAmount();
            return true;
        }
        return false;
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Order that = (Order) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    private void orderCanBeModified() {
        if (status != null && (status == OrderStatus.COMPLETED || status == OrderStatus.CANCELLED)) {
            throw new DomainException("Đơn hàng đã hoàn thành hoặc đã hủy không thể thay đổi");
        }
    }

    public OrderProduct addProduct(ProductPrice price,
                                   @Nullable String option,
                                   @Min(1) int quantity) {
        orderCanBeModified();

        var existingOrderProduct = findProduct(price);

        if (existingOrderProduct.isPresent()) {
            existingOrderProduct.get().setQuantity(
                    existingOrderProduct.get().getQuantity() + quantity
            );
            return existingOrderProduct.get();
        }

        var orderProduct = OrderProduct.builder()
                .price(price)
                .option(option)
                .quantity(quantity)
                .order(this)
                .id(OrderProductId.create().getValue())
                .build();

        orderProducts.add(orderProduct);

        recalculateTotalAmount();

        return orderProduct;

    }

    public boolean removeProduct(ProductPrice price) {
        orderCanBeModified();
        var existingOrderProduct = findProduct(price);

        if (existingOrderProduct.isPresent()) {
            orderProducts.remove(existingOrderProduct.get());
            recalculateTotalAmount();
            return true;
        }
        return false;
    }

    public OrderTable addTable(@NotNull ServiceTable orderTable) {
        orderCanBeModified();
        var existingOrderTable = findTable(orderTable);

        if (existingOrderTable.isPresent()) {
            return existingOrderTable.get();
        }

        var orderTableEntity = OrderTable.builder()
                .order(this)
                .table(orderTable)
                .checkIn(LocalDateTime.now())
                .checkOut(null)
                .id(OrderTableId.create().getValue())
                .build();

        orderTables.add(orderTableEntity);

        return orderTableEntity;
    }

    public boolean setStatus(@NotNull OrderStatus newStatus) {
        if (Objects.equals(this.status, newStatus)) {
            return false;
        }

        if (this.status == OrderStatus.COMPLETED || this.status == OrderStatus.CANCELLED) {
            throw new DomainException("Đơn hàng đã hoàn thành hoặc đã hủy, không thể thay đổi trạng thái");
        }

        if (newStatus == OrderStatus.COMPLETED && (totalAmount == null || finalAmount == null)) {
            throw new DomainException("Đơn hàng chưa có giá trị, không thể hoàn thành");
        }

        this.status = newStatus;

        return true;
    }

    public boolean setCustomizeNote(@Nullable String customizeNote) {
        orderCanBeModified();
        if (Objects.equals(this.customizeNote, customizeNote)) {
            return false;
        }
        this.customizeNote = customizeNote;
        return true;
    }

    public boolean setProductQuantity(@NotNull ProductPriceId productPriceId, int quantity) {
        orderCanBeModified();
        var product = orderProducts.stream()
                .filter(orderProductEntity -> ProductPriceId.of(orderProductEntity.getPrice().getId()).equals(productPriceId))
                .findFirst();

        if (product.isPresent()) {
            product.get().setQuantity(quantity);
            return true;
        }
        return false;
    }

    private Optional<OrderTable> findTable(ServiceTable orderTable) {
        return orderTables.stream()
                .filter(orderTableEntity -> orderTableEntity.getTable().getId().equals(orderTable.getId()))
                .findFirst();
    }


    private Optional<OrderProduct> findProduct(ProductPrice orderProduct) {
        return orderProducts.stream()
                .filter(orderProductEntity -> orderProductEntity.getPrice().getId().equals(orderProduct.getId()))
                .findFirst();
    }

    private Optional<OrderDiscount> findDiscount(Discount discount) {
        return orderDiscounts.stream()
                .filter(orderDiscount -> orderDiscount.getDiscount().getId().equals(discount.getId()))
                .findFirst();
    }

    private void recalculateTotalAmount() {
        Money subTotal = Money.ZERO;

        // Calculate subtotal from all order products
        for (OrderProduct orderProduct : orderProducts) {
            subTotal = subTotal.add(orderProduct.getPrice().getPrice()
                    .multiply(BigDecimal.valueOf(orderProduct.getQuantity())));
        }

        this.totalAmount = subTotal.getValue();

        // Track total discount amount
        Money totalDiscountAmount = Money.ZERO;

        // Calculate coupon discounts based on original total
        for (OrderDiscount orderDiscount : orderDiscounts) {
            var discountValue = orderDiscount.getDiscount().getPromotionDiscountValue();

            Money discountAmount = switch (discountValue.getUnit()) {
                case PERCENTAGE -> {
                    BigDecimal percentage = discountValue.getValue();
                    var maxDiscount = discountValue.getMaxDiscountAmount();
                    var discount = subTotal.multiply(percentage).divide(BigDecimal.valueOf(100));
                    if (discount.compareTo(maxDiscount) > 0) {
                        yield maxDiscount;
                    }
                    yield discount;
                }
                case FIXED -> {
                    yield Money.of(discountValue.getValue());
                }
            };

            totalDiscountAmount = totalDiscountAmount.add(discountAmount);
            orderDiscount.setDiscountAmount(discountAmount);
        }

        // Apply membership discount based on original total if customer exists
        if (getCustomer().isPresent()) {
            var discountMember = getCustomer().get().getMembershipType().getMemberDiscountValue();

            Money memberDiscountAmount = switch (discountMember.getUnit()) {
                case PERCENTAGE -> {
                    BigDecimal percentage = discountMember.getValue();
                    yield subTotal.multiply(percentage).divide(BigDecimal.valueOf(100));
                }
                case FIXED -> Money.of(discountMember.getValue());
            };

            totalDiscountAmount = totalDiscountAmount.add(memberDiscountAmount);
        }

        // Final amount is subtotal minus total discounts
        this.finalAmount = subTotal.subtract(totalDiscountAmount).getValue();
    }

    public void checkOut() {

        if (this.status != OrderStatus.COMPLETED) {
            throw new DomainException("Đơn hàng chưa hoàn thành, không thể lưu thời gian rời bàn");
        }
        for (OrderTable orderTable : orderTables) {
            orderTable.setCheckOut(LocalDateTime.now());
        }
    }

    public void cancel() {
        if (this.status == OrderStatus.COMPLETED) {
            throw new DomainException("Đơn hàng đã hoàn thành, không thể hủy");
        }

        this.status = OrderStatus.CANCELLED;
        for (OrderTable orderTable : orderTables) {
            orderTable.setCheckOut(null);
        }
    }

    @Transient
    public Optional<Money> getDiscountAmount() {
        if (getTotalAmount().isPresent() && getFinalAmount().isPresent()) {
            return Optional.of(getTotalAmount().get().subtract(getFinalAmount().get()));
        }
        return Optional.empty();
    }

    public static class OrderBuilder {
        private @NotNull Long id;
        private Customer customer;
        private @NotNull Employee employee;
        private @NotNull Instant orderTime;
        private BigDecimal totalAmount;
        private BigDecimal finalAmount;
        private String customizeNote;
        private OrderStatus status;
        private Set<OrderDiscount> orderDiscounts$value;
        private boolean orderDiscounts$set;
        private Set<OrderProduct> orderProducts$value;
        private boolean orderProducts$set;
        private Set<OrderTable> orderTables$value;
        private boolean orderTables$set;
        private @Min(value = 1) Long point;
        private Set<Payment> payments$value;
        private boolean payments$set;

        OrderBuilder() {
        }

        public OrderBuilder id(@NotNull Long id) {
            this.id = id;
            return this;
        }

        public OrderBuilder customer(Customer customer) {
            this.customer = customer;
            return this;
        }

        public OrderBuilder employee(@NotNull Employee employee) {
            this.employee = employee;
            return this;
        }

        public OrderBuilder orderTime(@NotNull Instant orderTime) {
            this.orderTime = orderTime;
            return this;
        }

        public OrderBuilder totalAmount(@Nullable Money totalAmount) {
            this.totalAmount = totalAmount == null ? null : totalAmount.getValue();
            return this;
        }

        public OrderBuilder finalAmount(@Nullable Money finalAmount) {
            this.finalAmount = finalAmount == null ? null : finalAmount.getValue();
            return this;
        }

        public OrderBuilder customizeNote(String customizeNote) {
            this.customizeNote = customizeNote;
            return this;
        }

        public OrderBuilder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public OrderBuilder orderDiscounts(Set<OrderDiscount> orderDiscounts) {
            this.orderDiscounts$value = orderDiscounts;
            this.orderDiscounts$set = true;
            return this;
        }

        public OrderBuilder orderProducts(Set<OrderProduct> orderProducts) {
            this.orderProducts$value = orderProducts;
            this.orderProducts$set = true;
            return this;
        }

        public OrderBuilder orderTables(Set<OrderTable> orderTables) {
            this.orderTables$value = orderTables;
            this.orderTables$set = true;
            return this;
        }

        public OrderBuilder point(@Min(value = 1) Long point) {
            this.point = point;
            return this;
        }

        public OrderBuilder payments(Set<Payment> payments) {
            this.payments$value = payments;
            this.payments$set = true;
            return this;
        }

        public Order build() {
            Set<OrderDiscount> orderDiscounts$value = this.orderDiscounts$value;
            if (!this.orderDiscounts$set) {
                orderDiscounts$value = Order.$default$orderDiscounts();
            }
            Set<OrderProduct> orderProducts$value = this.orderProducts$value;
            if (!this.orderProducts$set) {
                orderProducts$value = Order.$default$orderProducts();
            }
            Set<OrderTable> orderTables$value = this.orderTables$value;
            if (!this.orderTables$set) {
                orderTables$value = Order.$default$orderTables();
            }
            Set<Payment> payments$value = this.payments$value;
            if (!this.payments$set) {
                payments$value = Order.$default$payments();
            }
            return new Order(this.id, this.customer, this.employee, this.orderTime, this.totalAmount, this.finalAmount, this.customizeNote, this.status, orderDiscounts$value, orderProducts$value, orderTables$value, this.point, payments$value);
        }

        public String toString() {
            return "Order.OrderBuilder(id=" + this.id + ", customer=" + this.customer + ", employee=" + this.employee + ", orderTime=" + this.orderTime + ", totalAmount=" + this.totalAmount + ", finalAmount=" + this.finalAmount + ", customizeNote=" + this.customizeNote + ", status=" + this.status + ", orderDiscounts$value=" + this.orderDiscounts$value + ", orderProducts$value=" + this.orderProducts$value + ", orderTables$value=" + this.orderTables$value + ", point=" + this.point + ", payments$value=" + this.payments$value + ")";
        }
    }
}