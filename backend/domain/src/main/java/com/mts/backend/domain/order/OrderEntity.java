package com.mts.backend.domain.order;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.customer.CustomerEntity;
import com.mts.backend.domain.order.identifier.OrderDiscountId;
import com.mts.backend.domain.order.identifier.OrderProductId;
import com.mts.backend.domain.order.identifier.OrderTableId;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.product.ProductPriceEntity;
import com.mts.backend.domain.product.identifier.ProductPriceId;
import com.mts.backend.domain.promotion.DiscountEntity;
import com.mts.backend.domain.staff.EmployeeEntity;
import com.mts.backend.domain.store.ServiceTableEntity;
import com.mts.backend.shared.exception.DomainException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
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

@Getter
@Setter
@Entity
@Table(name = "`order`", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "employee_id", columnList = "employee_id")
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NamedEntityGraphs(
        @NamedEntityGraph(name = "OrderEntity.detail",
                attributeNodes = {
                        @NamedAttributeNode("orderDiscounts"),
                        @NamedAttributeNode("orderProducts"),
                        @NamedAttributeNode("orderTables")
                }
        )
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity extends BaseEntity<Long> {
    @Id
    @Comment("Mã đơn hàng")
    @Column(name = "order_id", columnDefinition = "int UNSIGNED")
    @NotNull
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã khách hàng")
    @JoinColumn(name = "customer_id")
    @Nullable
    private CustomerEntity customerEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã nhân viên")
    @JoinColumn(name = "employee_id", nullable = false)
    @NotNull
    private EmployeeEntity employeeEntity;
    @Comment("Thời gian đặt hàng")
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "order_time", nullable = false)
    @NotNull
    private Instant orderTime;
    @Comment("Tổng tiền")
    @Column(name = "total_amount", precision = 11, scale = 3)
    @Convert(converter = Money.MoneyConverter.class)
    @Nullable
    private Money totalAmount;
    @Comment("Thành tiền")
    @Column(name = "final_amount", precision = 11, scale = 3)
    @Convert(converter = Money.MoneyConverter.class)
    @Nullable
    private Money finalAmount;
    @Comment("Ghi chú tùy chỉnh")
    @Column(name = "customize_note", length = 1000)
    @Nullable
    private String customizeNote;
    @Comment("Trạng thái đơn hàng")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Nullable
    private OrderStatus status;
    @OneToMany(mappedBy = "orderEntity", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    @Builder.Default
    private Set<OrderDiscountEntity> orderDiscounts = new LinkedHashSet<>();
    @OneToMany(mappedBy = "orderEntity", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    @Builder.Default
    private Set<OrderProductEntity> orderProducts = new LinkedHashSet<>();
    @OneToMany(mappedBy = "orderEntity", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    @Builder.Default
    private Set<OrderTableEntity> orderTables = new LinkedHashSet<>();
    @ColumnDefault("'1'")
    @Column(name = "point", columnDefinition = "int UNSIGNED")
    @Nullable
    @Comment("Điểm thưởng")
    @Min(value = 1)
    private Long point;

    public Optional<CustomerEntity> getCustomerEntity() {
        return Optional.ofNullable(customerEntity);
    }

    public void setCustomerEntity(CustomerEntity customerEntity) {
        orderCanBeModified();
        this.customerEntity = customerEntity;
        recalculateTotalAmount();
    }

    public Optional<Money> getTotalAmount() {
        return Optional.ofNullable(totalAmount);
    }

    public Optional<Money> getFinalAmount() {
        return Optional.ofNullable(finalAmount);
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

    public OrderDiscountEntity addDiscount(DiscountEntity discount) {
        orderCanBeModified();
        var existingOrderDiscount = findOrderDiscount(discount);

        if (existingOrderDiscount.isPresent()) {
            return existingOrderDiscount.get();
        }

        var orderDiscount = OrderDiscountEntity.builder()
                .discountAmount(Money.ZERO)
                .id(OrderDiscountId.create().getValue())
                .discount(discount)
                .orderEntity(this)
                .build();
        discount.increaseCurrentUses();
        orderDiscounts.add(orderDiscount);
        recalculateTotalAmount();
        return orderDiscount;
    }

    public boolean removeDiscount(DiscountEntity discount) {
        orderCanBeModified();
        var existingOrderDiscount = findOrderDiscount(discount);

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
        OrderEntity that = (OrderEntity) o;
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

    public OrderProductEntity addOrderProduct(ProductPriceEntity price,
                                              @Nullable String option,
                                              @Min(1) int quantity) {
        orderCanBeModified();

        var existingOrderProduct = findOrderProduct(price);

        if (existingOrderProduct.isPresent()) {
            existingOrderProduct.get().setQuantity(
                    existingOrderProduct.get().getQuantity() + quantity
            );
            return existingOrderProduct.get();
        }

        var orderProduct = OrderProductEntity.builder()
                .productPriceEntity(price)
                .option(option)
                .quantity(quantity)
                .orderEntity(this)
                .id(OrderProductId.create().getValue())
                .build();

        orderProducts.add(orderProduct);

        recalculateTotalAmount();

        return orderProduct;

    }

    public boolean removeOrderProduct(ProductPriceEntity price) {
        orderCanBeModified();
        var existingOrderProduct = findOrderProduct(price);

        if (existingOrderProduct.isPresent()) {
            orderProducts.remove(existingOrderProduct.get());
            recalculateTotalAmount();
            return true;
        }
        return false;
    }

    public OrderTableEntity addOrderTable(ServiceTableEntity orderTable) {
        orderCanBeModified();
        var existingOrderTable = findOrderTable(orderTable);

        if (existingOrderTable.isPresent()) {
            return existingOrderTable.get();
        }

        var orderTableEntity = OrderTableEntity.builder()
                .orderEntity(this)
                .table(orderTable)
                .checkIn(LocalDateTime.now())
                .checkOut(null)
                .id(OrderTableId.create().getValue())
                .build();

        orderTables.add(orderTableEntity);

        return orderTableEntity;
    }

    public boolean changeStatus(OrderStatus newStatus) {
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

    public boolean changeCustomizeNote(String customizeNote) {
        orderCanBeModified();
        if (Objects.equals(this.customizeNote, customizeNote)) {
            return false;
        }
        this.customizeNote = customizeNote;
        return true;
    }

    public boolean changeProductQuantity(ProductPriceId productPriceId, int quantity) {
        orderCanBeModified();
        var existingOrderProduct = orderProducts.stream()
                .filter(orderProductEntity -> orderProductEntity.getProductPriceEntity().getId().equals(productPriceId))
                .findFirst();

        if (existingOrderProduct.isPresent()) {
            existingOrderProduct.get().setQuantity(quantity);
            return true;
        }
        return false;
    }

    private Optional<OrderTableEntity> findOrderTable(ServiceTableEntity orderTable) {
        return orderTables.stream()
                .filter(orderTableEntity -> orderTableEntity.getTable().getId().equals(orderTable.getId()))
                .findFirst();
    }


    private Optional<OrderProductEntity> findOrderProduct(ProductPriceEntity orderProduct) {
        return orderProducts.stream()
                .filter(orderProductEntity -> orderProductEntity.getProductPriceEntity().getId().equals(orderProduct.getId()))
                .findFirst();
    }

    private Optional<OrderDiscountEntity> findOrderDiscount(DiscountEntity discount) {
        return orderDiscounts.stream()
                .filter(orderDiscountEntity -> orderDiscountEntity.getDiscount().getId().equals(discount.getId()))
                .findFirst();
    }

    private void recalculateTotalAmount() {
        Money subTotal = Money.ZERO;

        // Calculate subtotal from all order products
        for (OrderProductEntity orderProduct : orderProducts) {
            subTotal = subTotal.add(orderProduct.getProductPriceEntity().getPrice()
                    .multiply(BigDecimal.valueOf(orderProduct.getQuantity())));
        }

        this.totalAmount = subTotal;

        // Track total discount amount
        Money totalDiscountAmount = Money.ZERO;

        // Calculate coupon discounts based on original total
        for (OrderDiscountEntity orderDiscount : orderDiscounts) {
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
                    yield Money.builder().value(discountValue.getValue()).build();
                }
            };

            totalDiscountAmount = totalDiscountAmount.add(discountAmount);
            orderDiscount.changeDiscountAmount(discountAmount);
        }

        // Apply membership discount based on original total if customer exists
        if (getCustomerEntity().isPresent()) {
            var discountMember = getCustomerEntity().get().getMembershipTypeEntity().getMemberDiscountValue();

            Money memberDiscountAmount = switch (discountMember.getUnit()) {
                case PERCENTAGE -> {
                    BigDecimal percentage = discountMember.getValue();
                    yield subTotal.multiply(percentage).divide(BigDecimal.valueOf(100));
                }
                case FIXED -> Money.builder().value(discountMember.getValue()).build();
            };

            totalDiscountAmount = totalDiscountAmount.add(memberDiscountAmount);
        }

        // Final amount is subtotal minus total discounts
        this.finalAmount = subTotal.subtract(totalDiscountAmount);
    }

    public void checkOut() {
        
        if (this.status != OrderStatus.COMPLETED) {
            throw new DomainException("Đơn hàng chưa hoàn thành, không thể lưu thời gian rời bàn");
        }
        for (OrderTableEntity orderTable : orderTables) {
            orderTable.setCheckOut(LocalDateTime.now());
        }
    }

    public void cancel() {
        if (this.status == OrderStatus.COMPLETED) {
            throw new DomainException("Đơn hàng đã hoàn thành, không thể hủy");
        }

        this.status = OrderStatus.CANCELLED;
        for (OrderTableEntity orderTable : orderTables) {
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

}