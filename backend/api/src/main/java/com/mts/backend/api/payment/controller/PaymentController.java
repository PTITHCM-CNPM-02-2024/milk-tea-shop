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
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController implements IController {
    
    private final PaymentCommandBus paymentCommandBus;
    private final PaymentQueryBus paymentQueryBus;
    
    public PaymentController(PaymentCommandBus paymentCommandBus, PaymentQueryBus paymentQueryBus) {
        this.paymentCommandBus = paymentCommandBus;
        this.paymentQueryBus = paymentQueryBus;
    }
    
    @PostMapping("/initiate")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> initiatePayment(@RequestBody CreatePaymentRequest request) {
        
        var command = CreatePaymentCommand.builder()
                .orderId(OrderId.of(request.getOrderId()))
                .paymentMethodId(PaymentMethodId.of(request.getPaymentMethodId()))
                .build();
        
        var result = paymentCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @PostMapping("/{paymentId}/{methodId}/complete")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> completePayment(@PathVariable("paymentId") Long paymentId,
                                             @PathVariable("methodId") Integer methodId,
                                             @RequestBody PaymentTransactionRequest request) {
        
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

    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<?> getPaymentById(@PathVariable("paymentId") Long paymentId) {
        var command = PaymentByIdQuery.builder()
                .paymentId(PaymentId.of(paymentId))
                .build();

        var result = paymentQueryBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<?> getAllPayments(@RequestParam(value = "page", defaultValue = "0") int page,
                                            @RequestParam(value = "size", defaultValue = "10") int size) {
        var command = DefaultPaymentQuery.builder()
                .page(page)
                .size(size)
                .build();

        var result = paymentQueryBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @GetMapping("/report")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getPaymentReportByMonth(@RequestParam(value = "year") Integer year,
                                                    @RequestParam(value = "month") Integer month) {
        var command = PaymentReportByMonthQuery.builder()
                .year(year)
                .month(month)
                .build();

        var result = paymentQueryBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    

    
}
