package com.mts.backend.api.product.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.product.request.CreateProductSizeRequest;
import com.mts.backend.api.product.request.UpdateProductSizeRequest;
import com.mts.backend.application.product.ProductSizeCommandBus;
import com.mts.backend.application.product.command.CreateProductSizeCommand;
import com.mts.backend.application.product.command.UpdateProductSizeCommand;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/product-sizes")
public class ProductSizeController implements IController {
    
    private final ProductSizeCommandBus productSizeCommandBus;
    
    public ProductSizeController(ProductSizeCommandBus productSizeCommandBus) {
        this.productSizeCommandBus = productSizeCommandBus;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Integer>> createProductSize(@RequestBody CreateProductSizeRequest request) {

        CreateProductSizeCommand command = CreateProductSizeCommand.builder().name(request.getName()).unitId(request.getUnitId()).description(request.getDescription()).quantity(request.getQuantity()).build();
        
        var result = productSizeCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Integer) result.getData())) : handleError(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Integer>> updateProductSize(@PathVariable("id") Integer id, @RequestBody UpdateProductSizeRequest request) {
        UpdateProductSizeCommand command = UpdateProductSizeCommand.builder().id(id).name(request.getName()).unitId(request.getUnitId()).description(request.getDescription()).quantity(request.getQuantity()).build();
        
        var result = productSizeCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Integer) result.getData(), "Thông tin sản phẩm đã được cập nhật")) : handleError(result);
    }
}
