package com.mts.backend.api.product.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.product.request.CreateProductSizeRequest;
import com.mts.backend.api.product.request.UpdateProductSizeRequest;
import com.mts.backend.application.product.ProductSizeCommandBus;
import com.mts.backend.application.product.SizeQueryBus;
import com.mts.backend.application.product.command.CreateProductSizeCommand;
import com.mts.backend.application.product.command.UpdateProductSizeCommand;
import com.mts.backend.application.product.query.DefaultSizeQuery;
import com.mts.backend.application.product.response.SizeDetailResponse;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<SizeDetailResponse>>> getAllProductSize() {
        DefaultSizeQuery query = DefaultSizeQuery.builder().build();
        
        var result = sizeQueryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((List<SizeDetailResponse>) result.getData())) : handleError(result);
        
    }
}
