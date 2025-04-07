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
import com.mts.backend.domain.common.value_object.DiscountUnit;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.identifier.DiscountId;
import com.mts.backend.domain.promotion.value_object.CouponCode;
import com.mts.backend.domain.promotion.value_object.DiscountName;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> createDiscount(@RequestBody CreateDiscountRequest request) {
        var command = CreateDiscountCommand.builder()
                .name(DiscountName.builder().value(request.getName()).build())
                .description(request.getDescription())
                .couponId(CouponId.of(request.getCouponId()))
                .discountUnit(DiscountUnit.valueOf(request.getDiscountUnit()))
                .discountValue(request.getDiscountValue())
                .maxDiscountAmount(Money.builder()
                        .value(request.getMaxDiscountAmount())
                             .build())
                .minimumOrderValue(Money.builder()
                        .value(request.getMinimumOrderValue())
                        .build())
                .minimumRequiredProduct(request.getMinimumRequiredProduct())
                .validFrom(request.getValidFrom())
                .validUntil(request.getValidUntil())
                .maxUsagePerCustomer(request.getMaxUsagePerCustomer())
                .maxUsage(request.getMaxUsage())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
        
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDiscount(@PathVariable("id") Long id, @RequestBody UpdateDiscountRequest request) {
        
        var command = UpdateDiscountCommand.builder()
                .id(DiscountId.of(id))
                .name(DiscountName.builder().value(request.getName()).build())
                .description(request.getDescription())
                .couponId(CouponId.of(request.getCouponId()))
                .discountUnit(DiscountUnit.valueOf(request.getDiscountUnit()))
                .discountValue(request.getDiscountValue())
                .maxDiscountAmount(Money.builder()
                        .value(request.getMaxDiscountAmount())
                        .build())
                .minimumOrderValue(Money.builder()
                        .value(request.getMinimumOrderValue())
                        .build())
                .minimumRequiredProduct(request.getMinimumRequiredProduct())
                .validFrom(request.getValidFrom())
                .validUntil(request.getValidUntil())
                .maxUsagePerCustomer(request.getMaxUsagePerCustomer())
                .maxUsage(request.getMaxUsage())
                .active(request.getActive())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllDiscount(@RequestParam(defaultValue = "0") Integer page,
                                                         @RequestParam(defaultValue = "10") Integer size) {
        var command = DefaultDiscountQuery.builder()
                .page(page)
                .size(size)
                .build();
        
        var result = queryBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getDiscountById(@PathVariable("id") Long id) {
        var command = DiscountByIdQuery.builder()
                .id(DiscountId.of(id))
                .build();
        
        var result = queryBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping("/coupon/{code}")
    public ResponseEntity<?> getDiscountByCouponCode(@PathVariable("code") String code) {
        var command = DiscountByCouponQuery.builder()
                .couponId(CouponCode.builder().value(code).build())
                .build();
        
        var result = queryBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
