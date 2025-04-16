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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.ArrayList;
import java.util.Objects;

@Tag(name = "Order Controller", description = "Order")
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController implements IController {
    private final OrderCommandBus commandBus;
    private final OrderQueryBus orderQueryBus;
    public OrderController(OrderCommandBus commandBus, OrderQueryBus orderQueryBus) {
        this.commandBus = commandBus;
        this.orderQueryBus = orderQueryBus;
    }

    @Operation(summary = "Tạo đơn hàng mới")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu đầu vào")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> createOrder(@Parameter(description = "Thông tin đơn hàng", required = true) @RequestBody OrderBaseRequest request) {

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

    @Operation(summary = "Hủy đơn hàng")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy đơn hàng")
    })
    @PutMapping("{orderId}/cancel")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> cancelOrder(@Parameter(description = "ID đơn hàng", required = true) @PathVariable("orderId") Long orderId) {
        var command = CancelledOrderCommand.builder()
                .id(OrderId.of(orderId))
                .build();

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);

    }
    
    @Operation(summary = "Tính toán đơn hàng (không lưu)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @PostMapping("/utilities/calculate")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> calculateOrder(@Parameter(description = "Thông tin đơn hàng", required = true) @RequestBody OrderBaseRequest request) {

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
    
    @Operation(summary = "Thanh toán đơn hàng")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy đơn hàng")
    })
    @PutMapping("{orderId}/checkout")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> checkoutOrder(@Parameter(description = "ID đơn hàng", required = true) @PathVariable("orderId") Long orderId) {
        var command = CheckOutOrderCommand.builder()
                .orderId(OrderId.of(orderId))
                .build();

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);

    }

    @Operation(summary = "Lấy danh sách đơn hàng")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<?> getAllOrders(@Parameter(description = "Trang", required = false) @RequestParam(value = "page", defaultValue = "0") Integer page,
                                        @Parameter(description = "Kích thước trang", required = false) @RequestParam(value = "size", defaultValue = "10") Integer size) {
        var command = DefaultOrderQuery.builder()
                .page(page)
                .size(size)
                .build();

        var result = orderQueryBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @Operation(summary = "Lấy đơn hàng theo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy đơn hàng")
    })
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getOrderById(@Parameter(description = "ID đơn hàng", required = true) @PathVariable("orderId") Long orderId) {
        var command = OrderByIdQuery.builder()
                .orderId(OrderId.of(orderId))
                .build();

        var result = orderQueryBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
