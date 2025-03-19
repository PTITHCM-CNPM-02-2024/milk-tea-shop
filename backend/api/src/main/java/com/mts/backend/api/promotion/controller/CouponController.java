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
    public ResponseEntity<ApiResponse<?>> createCoupon(@RequestBody CreateCouponRequest request) {
        var command = CreateCouponCommand.builder()
                .coupon(request.getCoupon())
                .description(request.getDescription())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) :handleError(result);
        
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateCoupon(@PathVariable("id") Long id, @RequestBody UpdateCouponRequest request) {
        
        var command = UpdateCouponCommand.builder()
                .id(id)
                .coupon(request.getCoupon())
                .description(request.getDescription())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllCoupon() {
        var command = DefaultCouponQuery.builder().build();
        
        var result = queryBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getCouponById(@PathVariable("id") Long id) {
        var command = CouponByIdQuery.builder()
                .id(id)
                .build();
        
        var result = queryBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
    }
}
