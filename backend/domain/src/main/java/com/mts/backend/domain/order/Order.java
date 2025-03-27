package com.mts.backend.domain.order;

import com.mts.backend.domain.common.value_object.MemberDiscountValue;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.order.entity.OrderDiscount;
import com.mts.backend.domain.order.entity.OrderProduct;
import com.mts.backend.domain.order.entity.OrderTable;
import com.mts.backend.domain.order.identifier.OrderDiscountId;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.identifier.OrderProductId;
import com.mts.backend.domain.order.identifier.OrderTableId;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.product.identifier.ProductPriceId;
import com.mts.backend.domain.promotion.identifier.DiscountId;
import com.mts.backend.domain.promotion.value_object.PromotionDiscountValue;
import com.mts.backend.domain.staff.identifier.EmployeeId;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import com.mts.backend.shared.exception.DomainBusinessLogicException;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

public class Order extends AbstractAggregateRoot<OrderId> {

    private final LocalDateTime createdAt = LocalDateTime.now();
    @NonNull
    private final Set<OrderProduct> orderProducts = new HashSet<>();
    @NonNull
    private final Set<OrderTable> orderTables = new HashSet<>();
    @NonNull
    private final Set<OrderDiscount> orderDiscounts = new HashSet<>();
    @Nullable
    private CustomerId customerId;
    @NonNull
    private EmployeeId employeeId;
    @NonNull
    private final Instant orderTime;
    @Nullable
    private Money totalAmount;
    @Nullable
    private Money finalAmount;
    @Nullable
    private OrderStatus status;
    @Nullable
    private String customizeNote;
    @Nullable
    private MemberDiscountValue memberDiscount;
    private LocalDateTime updatedAt;

    public Order(OrderId id,
                 @Nullable CustomerId customerId,
                 @NonNull EmployeeId employeeId,
                 @NonNull Instant orderTime,
                 @Nullable Money totalAmount,
                 @Nullable Money finalAmount,
                 @Nullable OrderStatus status,
                 @Nullable String customizeNote,
                 @NonNull Set<OrderProduct> orderProducts,
                 @NonNull Set<OrderTable> orderTables,
                 @NonNull Set<OrderDiscount> orderDiscounts,
                 @Nullable MemberDiscountValue memberDiscount,
                 LocalDateTime updatedAt) {
        super(id);
        this.customerId = customerId;
        this.employeeId = Objects.requireNonNull(employeeId, "Employee id is required");
        this.orderTime = orderTime;
        this.totalAmount = totalAmount;
        this.finalAmount = finalAmount;
        this.status = status;
        this.customizeNote = customizeNote;
        this.memberDiscount = customerId != null ? memberDiscount : null;
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();

        this.orderProducts.addAll(orderProducts);
        this.orderTables.addAll(orderTables);
        this.orderDiscounts.addAll(orderDiscounts);
        
        //valid();
    }

    private void valid(){
        List<String> errors = new ArrayList<>();

        if (orderProducts.isEmpty()){
            errors.add("Đơn hàng phải có ít nhất một sản phẩm");
        }

        if((status != null && status.compareTo(OrderStatus.COMPLETED) == 0) && totalAmount == null && finalAmount == null){
            errors.add("Đơn hàng đã hoàn thành nhưng chưa có giá trị");
        }

        if (!errors.isEmpty()){
            throw new DomainBusinessLogicException(errors);
        }
    }

    private void orderCanBeModified() {
        if (this.status != null && (this.status == OrderStatus.COMPLETED || this.status == OrderStatus.CANCELLED)) {
            throw new DomainException("Đơn hàng đã hoàn thành hoặc đã hủy, không thể chỉnh sửa");
        }
    }

    private boolean canApplyDiscountMember() {
        return customerId != null && memberDiscount != null;
    }

    public OrderProduct addProduct(@NonNull ProductPriceId productPriceId,int quantity,@NonNull String option,
                                   Money price) {
        orderCanBeModified();

        if (quantity <= 0) {
            throw new DomainException("Số lượng sản phẩm phải lớn hơn 0");
        }

        var existingProduct = findProductById(productPriceId);

        if (existingProduct.isPresent()) {
            OrderProduct orderProduct = existingProduct.get();
            orderProduct.changeQuantity(orderProduct.getQuantity() + quantity);
            this.updatedAt = LocalDateTime.now();
            return orderProduct;
        }

        OrderProduct orderProduct = new OrderProduct(OrderProductId.create(), getId(), productPriceId, quantity, option, price, LocalDateTime.now());


        orderProducts.add(orderProduct);
        recalculateOrderAmount();
        this.updatedAt = LocalDateTime.now();
        return orderProduct;
    }


    public boolean removeProduct(OrderProductId orderProductId) {
        orderCanBeModified();

        Optional<OrderProduct> productRemove = findOrderProductById(orderProductId);

        if (productRemove.isEmpty()) {
            return false;
        }

        orderProducts.remove(productRemove.get());
        recalculateOrderAmount();  
        this.updatedAt = LocalDateTime.now();

        return true;

    }

    public OrderTable addTable(ServiceTableId serviceTableId) {
        orderCanBeModified();
        Objects.requireNonNull(serviceTableId, "Service table id is required");

        findTableById(serviceTableId).orElseThrow(() -> new DuplicateException("Bàn" + serviceTableId + "đã tồn tại trong đơn hàng"));

        OrderTable orderTable = new OrderTable(OrderTableId.create(), getId(), serviceTableId, LocalDateTime.now(), null,
                LocalDateTime.now());
        
        orderTables.add(orderTable);
        this.updatedAt = LocalDateTime.now();
        return orderTable;
        
    }
    
    public boolean removeTable(OrderTableId orderTableId) {
        orderCanBeModified();
        
        Optional<OrderTable> tableRemove = findOrderTableById(orderTableId);
        
        if (tableRemove.isEmpty()) {
            return false;
        }
        
        orderTables.remove(tableRemove.get());
        
        this.updatedAt = LocalDateTime.now();
        
        return true;
    }
    
    public boolean checkOutTable(OrderTableId orderTableId) {
        
        Optional<OrderTable> tableCheckOut = findOrderTableById(orderTableId);
        
        if (tableCheckOut.isEmpty()) {
            return false;
        }
        
        tableCheckOut.get().checkOut();
        this.updatedAt = LocalDateTime.now();
        
        return true;
    }
    
    public boolean checkOutAllTable() {
        orderTables.forEach(OrderTable::checkOut);
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public OrderDiscount addDiscount(DiscountId discountId, PromotionDiscountValue discountValue) {
        orderCanBeModified();
        Objects.requireNonNull(discountId, "Discount id is required");
        Objects.requireNonNull(discountValue, "Discount value is required");
        
        findDiscountById(discountId).ifPresent(discount -> {
            throw new DuplicateException("Giảm giá" + discountId + "đã tồn tại trong đơn hàng");
        });
        
        OrderDiscount orderDiscount = new OrderDiscount(OrderDiscountId.create(), getId(), discountId, Money.ZERO, discountValue, LocalDateTime.now());
        
        orderDiscounts.add(orderDiscount);
        recalculateOrderAmount();
        this.updatedAt = LocalDateTime.now();
        return orderDiscount;
    }
    
    public boolean removeDiscount(OrderDiscountId orderDiscountId) {
        orderCanBeModified();
        
        Optional<OrderDiscount> discountRemove = findOrderDiscountById(orderDiscountId);
        
        if (discountRemove.isEmpty()) {
            return false;
        }
        
        orderDiscounts.remove(discountRemove.get());
        recalculateOrderAmount();
        this.updatedAt = LocalDateTime.now();
        
        return true;
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
        this.updatedAt = LocalDateTime.now();
        return true;
    }

    public boolean changeCustomizeNote(String customizeNote) {
        orderCanBeModified();

        if (Objects.equals(this.customizeNote, customizeNote)) {
            return false;
        }

        this.customizeNote = customizeNote;
        this.updatedAt = LocalDateTime.now();
        return true;
    }


    public boolean changeProductQuantity(OrderProductId orderProductId, int quantity) {
        orderCanBeModified();

        if (quantity <= 0) {
            throw new DomainException("Số lượng sản phẩm phải lớn hơn 0");
        }

        Optional<OrderProduct> productUpdate = findOrderProductById(orderProductId);

        if (productUpdate.isEmpty()) {
            return false;
        }

        OrderProduct orderProduct = productUpdate.get();
        orderProduct.changeQuantity(quantity);
        recalculateOrderAmount();
        this.updatedAt = LocalDateTime.now();
        return true;
    }

    public boolean changeCustomerId(CustomerId customerId, MemberDiscountValue memberDiscount) {
        orderCanBeModified();

        if (Objects.equals(this.customerId, customerId)) {
            return false;
        }

        this.customerId = customerId;
        this.memberDiscount = customerId != null ? memberDiscount : null;
        this.updatedAt = LocalDateTime.now();
        recalculateOrderAmount();
        return true;
    }

    public boolean changeEmployeeId(EmployeeId employeeId) {
        orderCanBeModified();

        if (Objects.equals(this.employeeId, employeeId)) {
            return false;
        }

        this.employeeId = Objects.requireNonNull(employeeId, "Employee id is required");
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public Optional<Money> getTotalAmount() {
        return Optional.ofNullable(totalAmount);
    }
    
    public Optional<Money> getFinalAmount() {
        return Optional.ofNullable(finalAmount);
    }
    
    public Optional<OrderStatus> getStatus() {
        return Optional.ofNullable(status);
    }
    
    public Optional<String> getCustomizeNote() {
        return Optional.ofNullable(customizeNote);
    }
    
    public Instant getOrderTime() {
        return orderTime;
    }
    
    public EmployeeId getEmployeeId(){
        return employeeId;
    }
    
    public Optional<MemberDiscountValue> getMemberDiscount() {
        return Optional.ofNullable(memberDiscount);
    }
    
    public Optional<CustomerId> getCustomerId() {
        return Optional.ofNullable(customerId);
    }
    
    public Set<OrderProduct> getOrderProducts() {
        return Collections.unmodifiableSet(orderProducts);
    }
    
    public Set<OrderTable> getOrderTables() {
        return Collections.unmodifiableSet(orderTables);
    }
    
    public Set<OrderDiscount> getOrderDiscounts() {
        return Collections.unmodifiableSet(orderDiscounts);
    }

    private Optional<OrderProduct> findOrderProductById(OrderProductId orderProductId) {
        return orderProducts.stream().filter(orderProduct -> orderProduct.getId().equals(orderProductId)).findFirst();
    }

    private Optional<OrderProduct> findProductById(ProductPriceId productPriceId) {
        return orderProducts.stream().filter(orderProduct -> orderProduct.getProductPriceId().equals(productPriceId)).findFirst();
    }

    private Optional<OrderTable> findTableById(ServiceTableId serviceTableId) {
        return orderTables.stream().filter(orderTable -> orderTable.getTableId().equals(serviceTableId)).findFirst();
    }
    
    private Optional<OrderTable> findOrderTableById(OrderTableId orderTableId) {
        return orderTables.stream().filter(orderTable -> orderTable.getId().equals(orderTableId)).findFirst();
    }
    
    private Optional<OrderDiscount> findDiscountById(DiscountId discountId) {
        return orderDiscounts.stream().filter(orderDiscount -> orderDiscount.getDiscountId().equals(discountId)).findFirst();
    }
    
    private Optional<OrderDiscount> findOrderDiscountById(OrderDiscountId orderDiscountId) {
        return orderDiscounts.stream().filter(orderDiscount -> orderDiscount.getId().equals(orderDiscountId)).findFirst();
    }

    private void recalculateOrderAmount() {
        Money subtotal = Money.ZERO;

        for (OrderProduct product : orderProducts) {
            Money price = product.getPrice().orElseThrow(() -> new DomainException("Sản phẩm không có giá")).multiply(BigDecimal.valueOf(product.getQuantity()));
            subtotal = subtotal.add(price);
        }

        this.totalAmount = subtotal;
        Money finalTotal = subtotal;

        for (OrderDiscount discount : orderDiscounts) {
            var mayBeDiscountValue = discount.getDiscountValue().orElseThrow(() -> new DomainException("Giảm giá không có giá trị"));
            Money discountAmount = switch (mayBeDiscountValue.getUnit()) {
                case PERCENTAGE -> {
                    BigDecimal percentValue = mayBeDiscountValue.getValue();
//                    yield subtotal.multiply(percentValue).divide(BigDecimal.valueOf(100));
                    //check percent value money <= max discount value
                    var discountAmountValue = subtotal.multiply(percentValue).divide(BigDecimal.valueOf(100));
                    if (discountAmountValue.compareTo(mayBeDiscountValue.getMaxDiscountAmount()) > 0) {
                        yield mayBeDiscountValue.getMaxDiscountAmount();
                    } else {
                        yield discountAmountValue;
                    }
                }
                case FIXED -> Money.of(mayBeDiscountValue.getValue());
            };

            finalTotal = finalTotal.subtract(discountAmount);
            discount.changeDiscountAmount(discountAmount);
        }


        // Apply member discount if applicable
        if (canApplyDiscountMember()) {
            Money memberDiscountAmount = subtotal.multiply(memberDiscount.getValue()).divide(BigDecimal.valueOf(100));
            finalTotal = finalTotal.subtract(memberDiscountAmount);
        }

        if (finalTotal.compareTo(Money.ZERO) < 0) {
            finalTotal = Money.ZERO;
        }

        this.finalAmount  = finalTotal;
    }

    public void cancel() {
        if (this.status == OrderStatus.COMPLETED) {
            throw new DomainException("Đơn hàng đã hoàn thành, không thể hủy");
        }

        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }


}
