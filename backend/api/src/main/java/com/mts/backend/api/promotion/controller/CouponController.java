package com.mts.backend.api.promotion.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.promotion.request.CreateCouponRequest;
import com.mts.backend.api.promotion.request.UpdateCouponRequest;
import com.mts.backend.application.promotion.CouponCommandBus;
import com.mts.backend.application.promotion.CouponQueryBus;
import com.mts.backend.application.promotion.command.CreateCouponCommand;
import com.mts.backend.application.promotion.command.UpdateCouponCommand;
import com.mts.backend.application.promotion.query.CouponByIdQuery;
import com.mts.backend.application.promotion.query.DefaultCouponQuery;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.value_object.CouponCode;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController implements IController {
    
    private final CouponCommandBus commandBus;
    private final CouponQueryBus queryBus;
    
    public CouponController(CouponCommandBus commandBus, CouponQueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }
    
    @PostMapping
    public ResponseEntity<?> createCoupon(@RequestBody CreateCouponRequest request) {
        var command = CreateCouponCommand.builder()
                .coupon(CouponCode.builder()
                        .value(request.getCoupon())
                        .build())
                .description(request.getDescription())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
        
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCoupon(@PathVariable("id") Long id, @RequestBody UpdateCouponRequest request) {
        
        var command = UpdateCouponCommand.builder()
                .id(CouponId.of(id))
                .coupon(CouponCode.builder()
                        .value(request.getCoupon())
                        .build())
                .description(request.getDescription())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllCoupon(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", defaultValue = "10") Integer size) {
        var command = DefaultCouponQuery.builder().
                page(page)
                .size(size)
                .build();
        
        var result = queryBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getCouponById(@PathVariable("id") Long id) {
        var command = CouponByIdQuery.builder()
                .id(CouponId.of(id))
                .build();
        
        var result = queryBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
