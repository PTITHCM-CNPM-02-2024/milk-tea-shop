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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Tag(name = "Discount Controller", description = "Discount")
@RestController
@RequestMapping("/api/v1/discounts")
public class DiscountController implements IController {
    private final DiscountCommandBus commandBus;
    private final DiscountQueryBus queryBus;

    public DiscountController(DiscountCommandBus commandBus, DiscountQueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }

    @Operation(summary = "Tạo khuyến mãi mới")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu đầu vào")
    })
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createDiscount(@Parameter(description = "Thông tin khuyến mãi", required = true) @RequestBody CreateDiscountRequest request) {
        var command = CreateDiscountCommand.builder()
                .name(DiscountName.of(request.getName()))
                .description(Objects.isNull(request.getDescription()) ? null : request.getDescription())
                .couponId(CouponId.of(request.getCouponId()))
                .discountUnit(DiscountUnit.valueOf(request.getDiscountUnit()))
                .discountValue(request.getDiscountValue())
                .maxDiscountAmount(Money.of
                        (request.getMaxDiscountAmount())
                )
                .minimumOrderValue(Money.of(request.getMinimumOrderValue()))
                .minimumRequiredProduct(request.getMinimumRequiredProduct())
                .validFrom(Objects.isNull(request.getValidFrom()) ? null : request.getValidFrom())
                .validUntil(request.getValidUntil())
                .maxUsagePerCustomer(Objects.isNull(request.getMaxUsagePerCustomer()) ? null : request.getMaxUsagePerCustomer())
                .maxUsage(Objects.isNull(request.getMaxUsage()) ? null : request.getMaxUsage())
                .build();

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);

    }

    @Operation(summary = "Cập nhật khuyến mãi")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khuyến mãi")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateDiscount(@Parameter(description = "ID khuyến mãi", required = true) @PathVariable("id") Long id, @Parameter(description = "Thông tin cập nhật", required = true) @RequestBody UpdateDiscountRequest request) {

        var command = UpdateDiscountCommand.builder()
                .id(DiscountId.of(id))
                .name(DiscountName.of(request.getName()))
                .description(Objects.isNull(request.getDescription()) ? null : request.getDescription())
                .couponId(CouponId.of(request.getCouponId()))
                .discountUnit(DiscountUnit.valueOf(request.getDiscountUnit()))
                .discountValue(request.getDiscountValue())
                .maxDiscountAmount(Money.of(request.getMaxDiscountAmount()))
                .minimumOrderValue(Money.of(request.getMinimumOrderValue()))
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

    @Operation(summary = "Lấy danh sách khuyến mãi")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getAllDiscount(@Parameter(description = "Trang", required = false) @RequestParam(value = "page", defaultValue = "0") Integer page,
                                            @Parameter(description = "Kích thước trang", required = false) @RequestParam(value = "size", defaultValue = "10") Integer size) {
        var command = DefaultDiscountQuery.builder()
                .page(page)
                .size(size)
                .build();

        var result = queryBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Lấy khuyến mãi theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khuyến mãi")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getDiscountById(@Parameter(description = "ID khuyến mãi", required = true) @PathVariable("id") Long id) {
        var command = DiscountByIdQuery.builder()
                .id(DiscountId.of(id))
                .build();

        var result = queryBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Lấy khuyến mãi theo mã coupon")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping("/coupon/{code}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getDiscountByCouponCode(@Parameter(description = "Mã coupon", required = true) @PathVariable("code") String code) {
        var command = DiscountByCouponQuery.builder()
                .couponId(CouponCode.of(code))
                .build();

        var result = queryBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Xóa khuyến mãi")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khuyến mãi")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteDiscount(@Parameter(description = "ID khuyến mãi", required = true) @PathVariable("id") Long id) {
        DeleteDiscountByIdCommand command = DeleteDiscountByIdCommand.builder()
                .discountId(DiscountId.of(id))
                .build();

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
