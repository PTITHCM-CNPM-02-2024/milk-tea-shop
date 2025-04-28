package com.mts.backend.application.order.query_handler;

import com.mts.backend.application.order.query.OrderByIdQuery;
import com.mts.backend.application.order.response.*;
import com.mts.backend.application.payment.response.PaymentDetailResponse;
import com.mts.backend.application.payment.response.PaymentMethodDetailResponse;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.customer.Customer;
import com.mts.backend.domain.order.jpa.JpaOrderRepository;
import com.mts.backend.domain.staff.Employee;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetOrderByIdQueryHandler implements IQueryHandler<OrderByIdQuery, CommandResult> {
    
    private final JpaOrderRepository orderRepository;
    
    public GetOrderByIdQueryHandler(JpaOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(OrderByIdQuery query) {
        Objects.requireNonNull(query.getOrderId(), "Order id is required");
        
        var order = orderRepository.findByIdFetch(query.getOrderId().getValue())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng với id: " + query.getOrderId().getValue()));
        
        
        var result = OrderDetailResponse.builder()
                .orderId(order.getId())
                .employeeId(order.getEmployee().map(Employee::getId).orElse(null))
                .employeeName(order.getEmployee().map(Employee::getFullName).orElse(null))
                .customerName(order.getCustomer().flatMap(Customer::getFullName).orElse(null))
                .orderStatus(order.getStatus().map(Enum::name).orElse(null))
                .totalAmount(order.getTotalAmount().map(Money::getValue).orElse(null))
                .finalAmount(order.getFinalAmount().map(Money::getValue).orElse(null))
                .orderTime(order.getOrderTime())
                .orderTables(order.getOrderTables().stream()
                        .map(orderTable -> OrderTableDetailResponse.builder()
                                .tableNumber(orderTable.getTable().getTableNumber().getValue())
                                .checkIn(orderTable.getCheckIn())
                                .checkOut(orderTable.getCheckOut())
                                .build())
                        .toList())
                .orderProducts(order.getOrderProducts().stream()
                        .map(orderProduct -> OrderProductDetailResponse.builder()
                                .productName(orderProduct.getPrice().getProduct().getName().getValue())
                                .quantity(orderProduct.getQuantity())
                                .price(orderProduct.getPrice().getPrice().getValue())
                                .quantity(orderProduct.getQuantity())
                                .build())
                        .toList())
                .orderDiscounts(order.getOrderDiscounts().stream()
                        .map(orderDiscount -> OrderDiscountDetailResponse.builder()
                                .name(orderDiscount.getDiscount().getName().getValue())
                                .couponCode(orderDiscount.getDiscount().getCoupon().getCoupon().getValue())
                                .discountValue(orderDiscount.getDiscount().getPromotionDiscountValue().getDescription())
                                .discountAmount(orderDiscount.getDiscountAmount().getValue())
                                .build())
                        .toList())
                .orderPayments(order.getPayments().stream().map(orderPayment -> PaymentDetailResponse.builder()
                        .paymentMethod(PaymentMethodDetailResponse.builder()
                                .id(orderPayment.getPaymentMethod().getId())
                                .name(orderPayment.getPaymentMethod().getName().getValue())
                                .description(orderPayment.getPaymentMethod().getDescription().orElse(null))
                                .build())
                        .amountPaid(orderPayment.getAmountPaid().map(Money::getValue).orElse(null))
                        .change(orderPayment.getChangeAmount().map(Money::getValue).orElse(null))
                        .status(orderPayment.getStatus().map(Enum::name).orElse(null))
                        .build())
                        .toList())
                .build();
        
        return CommandResult.success(result);
    }
}
