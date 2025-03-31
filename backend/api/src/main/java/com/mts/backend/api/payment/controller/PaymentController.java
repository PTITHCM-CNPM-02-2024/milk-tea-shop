package com.mts.backend.api.payment.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.payment.request.CreatePaymentRequest;
import com.mts.backend.api.payment.request.PaymentTransactionRequest;
import com.mts.backend.application.payment.PaymentCommandBus;
import com.mts.backend.application.payment.command.CreatePaymentCommand;
import com.mts.backend.application.payment.command.PaymentTransactionCommand;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.payment.identifier.PaymentId;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController implements IController {
    
    private final PaymentCommandBus paymentCommandBus;
    
    public PaymentController(PaymentCommandBus paymentCommandBus) {
        this.paymentCommandBus = paymentCommandBus;
    }
    
    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<?>> initiatePayment(@RequestBody CreatePaymentRequest request) {
        
        var command = CreatePaymentCommand.builder()
                .orderId(OrderId.of(request.getOrderId()))
                .paymentMethodId(PaymentMethodId.of(request.getPaymentMethodId()))
                .build();
        
        var result = paymentCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
    }
    
    @PostMapping("/{paymentId}/{methodId}/complete")
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
    
}
