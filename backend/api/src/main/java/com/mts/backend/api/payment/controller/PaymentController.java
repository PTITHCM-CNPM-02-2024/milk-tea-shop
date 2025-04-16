package com.mts.backend.api.payment.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.payment.request.CreatePaymentRequest;
import com.mts.backend.api.payment.request.PaymentTransactionRequest;
import com.mts.backend.application.payment.PaymentCommandBus;
import com.mts.backend.application.payment.PaymentQueryBus;
import com.mts.backend.application.payment.command.CreatePaymentCommand;
import com.mts.backend.application.payment.command.PaymentTransactionCommand;
import com.mts.backend.application.payment.query.DefaultPaymentQuery;
import com.mts.backend.application.payment.query.PaymentByIdQuery;
import com.mts.backend.application.payment.query.PaymentReportByMonthQuery;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.payment.identifier.PaymentId;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "Payment Controller", description = "Payment")
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController implements IController {
    
    private final PaymentCommandBus paymentCommandBus;
    private final PaymentQueryBus paymentQueryBus;
    
    public PaymentController(PaymentCommandBus paymentCommandBus, PaymentQueryBus paymentQueryBus) {
        this.paymentCommandBus = paymentCommandBus;
        this.paymentQueryBus = paymentQueryBus;
    }
    
    @Operation(summary = "Khởi tạo thanh toán mới")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu đầu vào")
    })
    @PostMapping("/initiate")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> initiatePayment(@Parameter(description = "Thông tin thanh toán", required = true) @RequestBody CreatePaymentRequest request) {
        
        var command = CreatePaymentCommand.builder()
                .orderId(OrderId.of(request.getOrderId()))
                .paymentMethodId(PaymentMethodId.of(request.getPaymentMethodId()))
                .build();
        
        var result = paymentCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @Operation(summary = "Hoàn tất thanh toán")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy thanh toán")
    })
    @PostMapping("/{paymentId}/{methodId}/complete")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> completePayment(
        @Parameter(description = "ID thanh toán", required = true) @PathVariable("paymentId") Long paymentId,
        @Parameter(description = "ID phương thức", required = true) @PathVariable("methodId") Integer methodId,
        @Parameter(description = "Thông tin giao dịch", required = true) @RequestBody PaymentTransactionRequest request) {
        
    var command = PaymentTransactionCommand.builder()
            .paymentId(PaymentId.of(paymentId))
            .paymentMethodId(PaymentMethodId.of(methodId))
            .transactionId(System.currentTimeMillis())
            .amount(Money.builder().value(request.getAmount()).build())
            .build();
    
    var result = paymentCommandBus.dispatch(command);
    
    return result.isSuccess() ? ResponseEntity.ok().contentType(MediaType.TEXT_HTML)
            .body(result.getData()) : handleError(result);
    }

    @Operation(summary = "Lấy thông tin thanh toán theo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy thanh toán")
    })
    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<?> getPaymentById(@Parameter(description = "ID thanh toán", required = true) @PathVariable("paymentId") Long paymentId) {
        var command = PaymentByIdQuery.builder()
                .paymentId(PaymentId.of(paymentId))
                .build();

        var result = paymentQueryBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Lấy danh sách thanh toán")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<?> getAllPayments(
        @Parameter(description = "Trang", required = false) @RequestParam(value = "page", defaultValue = "0") int page,
        @Parameter(description = "Kích thước trang", required = false) @RequestParam(value = "size", defaultValue = "10") int size) {
        var command = DefaultPaymentQuery.builder()
                .page(page)
                .size(size)
                .build();

        var result = paymentQueryBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Báo cáo thanh toán theo tháng")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping("/report")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getPaymentReportByMonth(
        @Parameter(description = "Năm", required = true) @RequestParam(value = "year") Integer year,
        @Parameter(description = "Tháng", required = true) @RequestParam(value = "month") Integer month) {
        var command = PaymentReportByMonthQuery.builder()
                .year(year)
                .month(month)
                .build();

        var result = paymentQueryBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    

    
}
