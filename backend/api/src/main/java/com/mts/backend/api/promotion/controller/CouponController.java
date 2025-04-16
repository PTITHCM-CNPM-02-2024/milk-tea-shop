package com.mts.backend.api.promotion.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.promotion.request.CreateCouponRequest;
import com.mts.backend.api.promotion.request.UpdateCouponRequest;
import com.mts.backend.application.promotion.CouponCommandBus;
import com.mts.backend.application.promotion.CouponQueryBus;
import com.mts.backend.application.promotion.command.CreateCouponCommand;
import com.mts.backend.application.promotion.command.DeleteCouponByIdCommand;
import com.mts.backend.application.promotion.command.UpdateCouponCommand;
import com.mts.backend.application.promotion.query.CouponByIdQuery;
import com.mts.backend.application.promotion.query.DefaultCouponQuery;
import com.mts.backend.application.promotion.query.UnusedCouponQuery;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.value_object.CouponCode;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Coupon Controller", description = "Coupon")
@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController implements IController {
    
    private final CouponCommandBus commandBus;
    private final CouponQueryBus queryBus;
    
    public CouponController(CouponCommandBus commandBus, CouponQueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }
    
    @Operation(summary = "Tạo coupon mới")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu đầu vào")
    })
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createCoupon(@Parameter(description = "Thông tin coupon", required = true) @RequestBody CreateCouponRequest request) {
        var command = CreateCouponCommand.builder()
                .coupon(CouponCode.builder()
                        .value(request.getCoupon())
                        .build())
                .description(request.getDescription())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
        
    }
    
    @Operation(summary = "Cập nhật coupon")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy coupon")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateCoupon(@Parameter(description = "ID coupon", required = true) @PathVariable("id") Long id, @Parameter(description = "Thông tin cập nhật", required = true) @RequestBody UpdateCouponRequest request) {
        
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
    
    @Operation(summary = "Lấy danh sách coupon")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getAllCoupon(@Parameter(description = "Trang", required = false) @RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @Parameter(description = "Kích thước trang", required = false) @RequestParam(value = "size", defaultValue = "10") Integer size) {
        var command = DefaultCouponQuery.builder().
                page(page)
                .size(size)
                .build();
        
        var result = queryBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @Operation(summary = "Lấy coupon theo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy coupon")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCouponById(@Parameter(description = "ID coupon", required = true) @PathVariable("id") Long id) {
        var command = CouponByIdQuery.builder()
                .id(CouponId.of(id))
                .build();
        
        var result = queryBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Lấy coupon chưa sử dụng")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping("/unused")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getUnusedCoupon(@Parameter(description = "Trang", required = false) @RequestParam(value = "page", defaultValue = "0") Integer page,
                                            @Parameter(description = "Kích thước trang", required = false) @RequestParam(value = "size", defaultValue = "10") Integer size) {
        var command = UnusedCouponQuery.builder().page(page).size(size).build();
        var result = queryBus.dispatch(command);
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Xóa coupon")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy coupon")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteCoupon(@Parameter(description = "ID coupon", required = true) @PathVariable("id") Long id) {
        DeleteCouponByIdCommand command = DeleteCouponByIdCommand.builder()
                .couponId(CouponId.of(id))
                .build();

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
