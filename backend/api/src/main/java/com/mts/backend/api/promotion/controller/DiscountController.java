package com.mts.backend.api.promotion.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.promotion.request.CreateDiscountRequest;
import com.mts.backend.api.promotion.request.UpdateDiscountRequest;
import com.mts.backend.application.promotion.DiscountCommandBus;
import com.mts.backend.application.promotion.DiscountQueryBus;
import com.mts.backend.application.promotion.command.CreateDiscountCommand;
import com.mts.backend.application.promotion.command.UpdateDiscountCommand;
import com.mts.backend.application.promotion.query.DefaultDiscountQuery;
import com.mts.backend.application.promotion.query.DiscountByCouponQuery;
import com.mts.backend.application.promotion.query.DiscountByIdQuery;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/discounts")
public class DiscountController implements IController {
    private final DiscountCommandBus commandBus;
    private final DiscountQueryBus queryBus;
    
    public DiscountController(DiscountCommandBus commandBus, DiscountQueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createDiscount(@RequestBody CreateDiscountRequest request) {
        var command = CreateDiscountCommand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .couponId(request.getCouponId())
                .discountUnit(request.getDiscountUnit())
                .discountValue(request.getDiscountValue())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .minimumOrderValue(request.getMinimumOrderValue())
                .minimumRequiredProduct(request.getMinimumRequiredProduct())
                .validFrom(request.getValidFrom())
                .validUntil(request.getValidUntil())
                .maxUsagePerCustomer(request.getMaxUsagePerCustomer())
                .maxUsage(request.getMaxUsage())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) :handleError(result);
        
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateDiscount(@PathVariable("id") Long id, @RequestBody UpdateDiscountRequest request) {
        
        var command = UpdateDiscountCommand.builder()
                .id(id)
                .name(request.getName())
                .description(request.getDescription())
                .couponId(request.getCouponId())
                .discountUnit(request.getDiscountUnit())
                .discountValue(request.getDiscountValue())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .minimumOrderValue(request.getMinimumOrderValue())
                .minimumRequiredProduct(request.getMinimumRequiredProduct())
                .validFrom(request.getValidFrom())
                .validUntil((request.getValidUntil()))
                .maxUsagePerCustomer(request.getMaxUsagePerCustomer())
                .maxUsage(request.getMaxUsage())
                .isActive(request.getIsActive())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllDiscount() {
        var command = DefaultDiscountQuery.builder().build();
        
        var result = queryBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getDiscountById(@PathVariable("id") Long id) {
        var command = DiscountByIdQuery.builder()
                .id(id)
                .build();
        
        var result = queryBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
    }
    
    @GetMapping("/coupon/{id}")
    public ResponseEntity<ApiResponse<?>> getDiscountByCouponCode(@PathVariable("id") Long id) {
        var command = DiscountByCouponQuery.builder()
                .couponId(id)
                .build();
        
        var result = queryBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
    }
    
    
    
}
