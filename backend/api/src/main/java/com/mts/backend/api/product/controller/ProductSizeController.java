package com.mts.backend.api.product.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.product.request.CreateProductSizeRequest;
import com.mts.backend.api.product.request.UpdateProductSizeRequest;
import com.mts.backend.application.product.ProductSizeCommandBus;
import com.mts.backend.application.product.SizeQueryBus;
import com.mts.backend.application.product.command.CreateProductSizeCommand;
import com.mts.backend.application.product.command.UpdateProductSizeCommand;
import com.mts.backend.application.product.query.DefaultSizeQuery;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.value_object.ProductSizeName;
import com.mts.backend.domain.product.value_object.QuantityOfProductSize;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/product-sizes")
public class ProductSizeController implements IController {

    private final ProductSizeCommandBus productSizeCommandBus;
    private final SizeQueryBus sizeQueryBus;

    public ProductSizeController(ProductSizeCommandBus productSizeCommandBus, SizeQueryBus sizeQueryBus) {
        this.productSizeCommandBus = productSizeCommandBus;
        this.sizeQueryBus = sizeQueryBus;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Integer>> createProductSize(@RequestBody CreateProductSizeRequest request) {

        CreateProductSizeCommand command = CreateProductSizeCommand.builder()
                .name(ProductSizeName.builder().value(request.getName()).build())
                .quantity(QuantityOfProductSize.builder().value(request.getQuantity()).build())
                .unitId(UnitOfMeasureId.of(request.getUnitId()))
                .description(request.getDescription())
                .build();


        var result = productSizeCommandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Integer) result.getData())) : handleError(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Integer>> updateProductSize(@PathVariable("id") Integer id, @RequestBody UpdateProductSizeRequest request) {
        UpdateProductSizeCommand command = UpdateProductSizeCommand.builder().
                id(ProductSizeId.of(id))
                .name(ProductSizeName.builder().value(request.getName()).build())
                .quantity(QuantityOfProductSize.builder().value(request.getQuantity()).build())
                .unitId(UnitOfMeasureId.of(request.getUnitId()))
                .description(request.getDescription())
                .build();

        var result = productSizeCommandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Integer) result.getData(), "Thông tin sản phẩm đã được cập nhật")) : handleError(result);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllProductSize() {
        DefaultSizeQuery query = DefaultSizeQuery.builder().build();

        var result = sizeQueryBus.dispatch(query);

        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);

    }
}
