package com.mts.backend.api.order.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.order.request.OrderBaseRequest;
import com.mts.backend.application.order.OrderCommandBus;
import com.mts.backend.application.order.command.*;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

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
                .employeeId(request.getEmployeeId())
                .orderProducts(new ArrayList<>())
                .orderDiscounts(new ArrayList<>())
                .orderTables(new ArrayList<>())
                .customerId(request.getCustomerId()).build();

        for (var orderProduct : request.getProducts()) {
            command.getOrderProducts().add(OrderProductCommand.builder()
                    .sizeId(orderProduct.getSizeId())
                    .productId(orderProduct.getProductId())
                    .quantity(orderProduct.getQuantity())
                    .build());
        }

        for (var orderDiscount : request.getDiscounts()) {
            command.getOrderDiscounts().add(OrderDiscountCommand.builder()
                    .discountId(orderDiscount.getDiscountId())
                    .build());
        }


        for (var orderTable : request.getTables()) {
            command.getOrderTables().add(OrderTableCommand.builder()
                    .serviceTableId(orderTable.getServiceTableId())
                    .build());
        }
        

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
    }

    
    @PutMapping("{orderId}/cancel")
    public ResponseEntity<ApiResponse<?>> cancelOrder(@PathVariable("orderId") CouponId orderId) {
        var command = CancelledOrderCommand.builder()
                .id(OrderId.of(orderId))
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
                
    }
}
