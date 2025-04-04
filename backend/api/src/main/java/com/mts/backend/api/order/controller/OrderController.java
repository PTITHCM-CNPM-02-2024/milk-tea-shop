package com.mts.backend.api.order.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.order.request.OrderBaseRequest;
import com.mts.backend.application.order.OrderCommandBus;
import com.mts.backend.application.order.command.*;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.promotion.identifier.DiscountId;
import com.mts.backend.domain.staff.identifier.EmployeeId;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController implements IController {
    private final OrderCommandBus commandBus;

    public OrderController(OrderCommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createOrder(@RequestBody OrderBaseRequest request) {

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

        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
    }


    @PutMapping("{orderId}/cancel")
    public ResponseEntity<ApiResponse<?>> cancelOrder(@PathVariable("orderId") Long orderId) {
        var command = CancelledOrderCommand.builder()
                .id(OrderId.of(orderId))
                .build();

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);

    }
    
    @PostMapping("/utilities/calculate")
    public ResponseEntity<ApiResponse<?>> calculateOrder(@RequestBody OrderBaseRequest request) {

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

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
    }
}
