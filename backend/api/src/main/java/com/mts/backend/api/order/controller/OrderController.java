package com.mts.backend.api.order.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.order.request.OrderBaseRequest;
import com.mts.backend.application.order.OrderCommandBus;
import com.mts.backend.application.order.command.*;
import com.mts.backend.application.order.query.DefaultOrderQuery;
import com.mts.backend.application.order.query.OrderByIdQuery;
import com.mts.backend.application.order.OrderQueryBus;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.promotion.identifier.DiscountId;
import com.mts.backend.domain.staff.identifier.EmployeeId;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController implements IController {
    private final OrderCommandBus commandBus;
    private final OrderQueryBus orderQueryBus;
    public OrderController(OrderCommandBus commandBus, OrderQueryBus orderQueryBus) {
        this.commandBus = commandBus;
        this.orderQueryBus = orderQueryBus;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> createOrder(@RequestBody OrderBaseRequest request) {

        var command = CreateOrderCommand.builder()
                .employeeId(EmployeeId.of(request.getEmployeeId()))
                .orderProducts(new ArrayList<>())
                .orderDiscounts(new ArrayList<>())
                .orderTables(new ArrayList<>())
                .customerId(Objects.isNull(request.getCustomerId()) ? null : CustomerId.of(request.getCustomerId()))
                .note(request.getNote())
                .build();

        for (var orderProduct : request.getProducts()) {
            command.getOrderProducts().add(OrderProductCommand.builder()
                    .sizeId(ProductSizeId.of(orderProduct.getSizeId()))
                    .productId(ProductId.of(orderProduct.getProductId()))
                    .quantity(orderProduct.getQuantity())
                    .option(orderProduct.getOption().orElse(null))
                    .build());
        }
        
        if (request.getTables() != null && !request.getTables().isEmpty()) {
            for (var orderTable : request.getTables()) {
                command.getOrderTables().add(OrderTableCommand.builder()
                        .serviceTableId(ServiceTableId.of(orderTable.getServiceTableId()))
                        .build());
            }
        }

       if (request.getDiscounts() != null && !request.getDiscounts().isEmpty()) {
            for (var orderDiscount : request.getDiscounts()) {
                command.getOrderDiscounts().add(OrderDiscountCommand.builder()
                        .discountId(DiscountId.of(orderDiscount.getDiscountId()))
                        .build());
            }
        }


        for (var orderTable : request.getTables()) {
            command.getOrderTables().add(OrderTableCommand.builder()
                    .serviceTableId(ServiceTableId.of(orderTable.getServiceTableId()))
                    .build());
        }


        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }


    @PutMapping("{orderId}/cancel")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> cancelOrder(@PathVariable("orderId") Long orderId) {
        var command = CancelledOrderCommand.builder()
                .id(OrderId.of(orderId))
                .build();

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);

    }
    
    @PostMapping("/utilities/calculate")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> calculateOrder(@RequestBody OrderBaseRequest request) {

        var command = CalculateOrderCommand.builder()
                .employeeId(EmployeeId.of(request.getEmployeeId()))
                .orderProducts(new ArrayList<>())
                .orderDiscounts(new ArrayList<>())
                .orderTables(new ArrayList<>())
                .customerId(Objects.isNull(request.getCustomerId()) ? null : CustomerId.of(request.getCustomerId()))
                .note(request.getNote())
                .build();

        for (var orderProduct : request.getProducts()) {
            command.getOrderProducts().add(OrderProductCommand.builder()
                    .sizeId(ProductSizeId.of(orderProduct.getSizeId()))
                    .productId(ProductId.of(orderProduct.getProductId()))
                    .quantity(orderProduct.getQuantity())
                    .build());
        }
        if (request.getDiscounts() != null && !request.getDiscounts().isEmpty()) {
            for (var orderDiscount : request.getDiscounts()) {
                command.getOrderDiscounts().add(OrderDiscountCommand.builder()
                        .discountId(DiscountId.of(orderDiscount.getDiscountId()))
                        .build());
            }
        }

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @PutMapping("{orderId}/checkout")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> checkoutOrder(@PathVariable("orderId") Long orderId) {
        var command = CheckOutOrderCommand.builder()
                .orderId(OrderId.of(orderId))
                .build();

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);

    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<?> getAllOrders(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size) {
        var command = DefaultOrderQuery.builder()
                .page(page)
                .size(size)
                .build();

        var result = orderQueryBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getOrderById(@PathVariable("orderId") Long orderId) {
        var command = OrderByIdQuery.builder()
                .orderId(OrderId.of(orderId))
                .build();

        var result = orderQueryBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
