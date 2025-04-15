package com.mts.backend.api.payment.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.payment.request.CreatePaymentMethodRequest;
import com.mts.backend.api.payment.request.UpdatePaymentMethodRequest;
import com.mts.backend.application.payment.PaymentMethodCommandBus;
import com.mts.backend.application.payment.PaymentMethodQueryBus;
import com.mts.backend.application.payment.command.CreatePaymentMethodCommand;
import com.mts.backend.application.payment.command.DeletePmtByIdCommand;
import com.mts.backend.application.payment.command.UpdatePaymentMethodCommand;
import com.mts.backend.application.payment.query.DefaultPaymentMethodQuery;
import com.mts.backend.application.payment.query.PaymentMethodByIdQuery;
import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.domain.payment.value_object.PaymentMethodName;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/v1/payment-methods")
public class PaymentMethodController implements IController {
    
    private final PaymentMethodCommandBus commandBus;
    private final PaymentMethodQueryBus queryBus;

    public PaymentMethodController(PaymentMethodCommandBus commandBus, PaymentMethodQueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createPaymentMethod(@RequestBody CreatePaymentMethodRequest request) {
        CreatePaymentMethodCommand command = CreatePaymentMethodCommand.builder()
                .name(PaymentMethodName.builder().value(request.getName()).build())
                .description(request.getDescription().orElse(null))
                .build();

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updatePaymentMethod(@PathVariable("id") Integer id,
                                                              @RequestBody UpdatePaymentMethodRequest request){
        UpdatePaymentMethodCommand command = UpdatePaymentMethodCommand.builder()
                .paymentMethodId(PaymentMethodId.of(id))
                .name(PaymentMethodName.builder().value(request.getName()).build())
                .description(request.getDescription().orElse(null))
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
        
    }
    
    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getAllPaymentMethod() {
        
        var command = new DefaultPaymentMethodQuery();
        
        var result = queryBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getPaymentMethodById(@PathVariable("id") Integer id) {
        var command = PaymentMethodByIdQuery.builder()
                .paymentMethodId(PaymentMethodId.of(id))
                .build();
        var result = queryBus.dispatch(command);
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deletePaymentMethod(@PathVariable("id") Integer id) {
        var command = DeletePmtByIdCommand.builder()
                .paymentMethodId(PaymentMethodId.of(id))
                .build();
        var result = commandBus.dispatch(command);
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
