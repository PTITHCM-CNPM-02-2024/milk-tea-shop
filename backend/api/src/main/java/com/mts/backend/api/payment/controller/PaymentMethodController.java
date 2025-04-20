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
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "Payment Method Controller", description = "Payment Method")
@RestController
@RequestMapping("/api/v1/payment-methods")
public class PaymentMethodController implements IController {
    
    private final PaymentMethodCommandBus commandBus;
    private final PaymentMethodQueryBus queryBus;

    public PaymentMethodController(PaymentMethodCommandBus commandBus, PaymentMethodQueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }
    
    @Operation(summary = "Tạo phương thức thanh toán mới")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu đầu vào")
    })
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createPaymentMethod(@Parameter(description = "Thông tin phương thức thanh toán", required = true) @RequestBody CreatePaymentMethodRequest request) {
        CreatePaymentMethodCommand command = CreatePaymentMethodCommand.builder()
                .name(PaymentMethodName.of(request.getName()))
                .description(request.getDescription().orElse(null))
                .build();

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @Operation(summary = "Cập nhật phương thức thanh toán")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy phương thức thanh toán")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updatePaymentMethod(@Parameter(description = "ID phương thức thanh toán", required = true) @PathVariable("id") Integer id,
                                                              @Parameter(description = "Thông tin cập nhật", required = true) @RequestBody UpdatePaymentMethodRequest request){
        UpdatePaymentMethodCommand command = UpdatePaymentMethodCommand.builder()
                .paymentMethodId(PaymentMethodId.of(id))
                .name(PaymentMethodName.of(request.getName()))
                .description(request.getDescription().orElse(null))
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
        
    }
    
    @Operation(summary = "Lấy danh sách phương thức thanh toán")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getAllPaymentMethod() {
        
        var command = new DefaultPaymentMethodQuery();
        
        var result = queryBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Lấy phương thức thanh toán theo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy phương thức thanh toán")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getPaymentMethodById(@Parameter(description = "ID phương thức thanh toán", required = true) @PathVariable("id") Integer id) {
        var command = PaymentMethodByIdQuery.builder()
                .paymentMethodId(PaymentMethodId.of(id))
                .build();
        var result = queryBus.dispatch(command);
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @Operation(summary = "Xóa phương thức thanh toán")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy phương thức thanh toán")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deletePaymentMethod(@Parameter(description = "ID phương thức thanh toán", required = true) @PathVariable("id") Integer id) {
        var command = DeletePmtByIdCommand.builder()
                .paymentMethodId(PaymentMethodId.of(id))
                .build();
        var result = commandBus.dispatch(command);
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
