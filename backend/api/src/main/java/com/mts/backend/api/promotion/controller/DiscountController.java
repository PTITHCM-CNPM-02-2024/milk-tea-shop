package com.mts.backend.api.promotion.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.promotion.request.CreateDiscountRequest;
import com.mts.backend.api.promotion.request.UpdateDiscountRequest;
import com.mts.backend.application.promotion.DiscountCommandBus;
import com.mts.backend.application.promotion.DiscountQueryBus;
import com.mts.backend.application.promotion.command.CreateDiscountCommand;
import com.mts.backend.application.promotion.command.DeleteDiscountByIdCommand;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

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
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createDiscount(@RequestBody CreateDiscountRequest request) {
        var command = CreateDiscountCommand.builder()
                .name(DiscountName.builder().value(request.getName()).build())
                .description(Objects.isNull(request.getDescription()) ? null : request.getDescription())
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
                .validFrom(Objects.isNull(request.getValidFrom()) ? null : request.getValidFrom())
                .validUntil(request.getValidUntil())
                .maxUsagePerCustomer(Objects.isNull(request.getMaxUsagePerCustomer()) ? null : request.getMaxUsagePerCustomer())
                .maxUsage(Objects.isNull(request.getMaxUsage()) ? null : request.getMaxUsage())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
        
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateDiscount(@PathVariable("id") Long id, @RequestBody UpdateDiscountRequest request) {
        
        var command = UpdateDiscountCommand.builder()
                .id(DiscountId.of(id))
                .name(DiscountName.builder().value(request.getName()).build())
                .description(Objects.isNull(request.getDescription()) ? null : request.getDescription())
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
                .validFrom(Objects.isNull(request.getValidFrom()) ? null : request.getValidFrom())
                .validUntil(request.getValidUntil())
                .maxUsagePerCustomer(request.getMaxUsagePerCustomer())
                .maxUsage(Objects.isNull(request.getMaxUsage()) ? null : request.getMaxUsage())
                .active(request.getActive())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getAllDiscount(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        var command = DefaultDiscountQuery.builder()
                .page(page)
                .size(size)
                .build();
        
        var result = queryBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getDiscountById(@PathVariable("id") Long id) {
        var command = DiscountByIdQuery.builder()
                .id(DiscountId.of(id))
                .build();
        
        var result = queryBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping("/coupon/{code}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getDiscountByCouponCode(@PathVariable("code") String code) {
        var command = DiscountByCouponQuery.builder()
                .couponId(CouponCode.builder().value(code).build())
                .build();
        
        var result = queryBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteDiscount(@PathVariable("id") Long id) {
        DeleteDiscountByIdCommand command = DeleteDiscountByIdCommand.builder()
                .discountId(DiscountId.of(id))
                .build();

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
