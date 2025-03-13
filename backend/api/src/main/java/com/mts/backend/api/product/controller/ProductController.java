package com.mts.backend.api.product.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.product.request.AddProductPriceRequest;
import com.mts.backend.api.product.request.UpdateProductInformRequest;
import com.mts.backend.api.product.request.CreateProductRequest;
import com.mts.backend.application.product.ProductCommandBus;
import com.mts.backend.application.product.command.*;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController implements IController {
    private final ProductCommandBus productCommandBus;
    
    public ProductController(ProductCommandBus productCommandBus) {
        this.productCommandBus = productCommandBus;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Integer>> createProduct(@RequestBody CreateProductRequest createProductRequest) {
        CreateProductInformCommand createProductInformCommand = CreateProductInformCommand.builder()
                .name(createProductRequest.getName())
                .description(createProductRequest.getDescription())
                .categoryId(createProductRequest.getCategoryId())
                .available(createProductRequest.isAvailable())
                .signature(createProductRequest.isSignature())
                .imagePath(createProductRequest.getImagePath())
                .build();
        
        var result = productCommandBus.dispatch(createProductInformCommand);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((int)result.getData())) : handleError(result);
        
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Integer>> updateProductInform(@PathVariable("id") Integer id, @RequestBody UpdateProductInformRequest updateProductInformRequest) {
        UpdateProductInformCommand updateProductInformCommand = UpdateProductInformCommand.builder()
                .productId(id)
                .name(updateProductInformRequest.getName())
                .description(updateProductInformRequest.getDescription())
                .categoryId(updateProductInformRequest.getCategoryId())
                .isAvailable(updateProductInformRequest.isAvailable())
                .isSignature(updateProductInformRequest.isSignature())
                .build();
        
        var result = productCommandBus.dispatch(updateProductInformCommand);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((int)result.getData(), "Thông tin sản phẩm đã được cập nhật")) : handleError(result);
    }
    
    @PostMapping("/{id}/prices")
    public ResponseEntity<ApiResponse<String>> createProductPrice(@PathVariable("id") Integer id, @RequestBody AddProductPriceRequest addProductPriceRequest) {

        AddProductPriceCommand addProductPriceCommand = AddProductPriceCommand.builder()
                .productId(id)
                .build();
        
        if (addProductPriceRequest.getProductPrices() != null){
            for (var productPrice : addProductPriceRequest.getProductPrices()) {
                addProductPriceCommand.getProductPrices().add(ProductPriceCommand.builder()
                        .sizeId(productPrice.getSizeId())
                        .price(productPrice.getPrice())
                        .build());
            }
        }
        
        var result = productCommandBus.dispatch(addProductPriceCommand);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success("Giá sản phẩm đã được thay đổi")) : handleError(result);
        
    }
    
    @PutMapping("/{id}/prices")
    public ResponseEntity<ApiResponse<String>> updateProductPrice(@PathVariable("id") Integer id, @RequestBody AddProductPriceRequest addProductPriceRequest) {
        UpdateProductPriceCommand updateProductPriceCommand = UpdateProductPriceCommand.builder()
                .productId(id)
                .build();
        
        if (addProductPriceRequest.getProductPrices() != null){
            for (var productPrice : addProductPriceRequest.getProductPrices()) {
                updateProductPriceCommand.getProductPrices().add(ProductPriceCommand.builder()
                        .sizeId(productPrice.getSizeId())
                        .price(productPrice.getPrice())
                        .build());
            }
        }
        
        var result = productCommandBus.dispatch(updateProductPriceCommand);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success("Giá sản phẩm đã được thay đổi")) : handleError(result);
    }
    
    @DeleteMapping("/{id}/prices/{sizeId}")
    public ResponseEntity<ApiResponse<String>> deleteProductPrice(@PathVariable("id") Integer id, @PathVariable("sizeId") Integer sizeId) {
        DeletePriceBySizeIdCommand deleteProductPriceCommand = DeletePriceBySizeIdCommand.builder()
                .productId(id)
                .sizeId(sizeId)
                .build();
        
        var result = productCommandBus.dispatch(deleteProductPriceCommand);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success("Giá sản phẩm đã được xóa")) : handleError(result);
    }
}
