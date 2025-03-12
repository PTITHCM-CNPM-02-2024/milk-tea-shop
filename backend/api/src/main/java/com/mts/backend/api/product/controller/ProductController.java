package com.mts.backend.api.product.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.product.request.AddProductPriceRequest;
import com.mts.backend.api.product.request.ChangeProductInformRequest;
import com.mts.backend.api.product.request.CreateProductRequest;
import com.mts.backend.application.product.ProductCommandBus;
import com.mts.backend.application.product.command.AddProductPriceCommand;
import com.mts.backend.application.product.command.ChangeProductInformCommand;
import com.mts.backend.application.product.command.CreateProductInformCommand;
import com.mts.backend.application.product.command.ProductPriceCommand;
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
    public ResponseEntity<ApiResponse<String>> changeProductInform(@PathVariable("id") Integer id, @RequestBody ChangeProductInformRequest changeProductInformRequest) {
        ChangeProductInformCommand changeProductInformCommand = ChangeProductInformCommand.builder()
                .productId(id)
                .name(changeProductInformRequest.getName())
                .description(changeProductInformRequest.getDescription())
                .categoryId(changeProductInformRequest.getCategoryId())
                .isAvailable(changeProductInformRequest.isAvailable())
                .isSignature(changeProductInformRequest.isSignature())
                .build();
        
        var result = productCommandBus.dispatch(changeProductInformCommand);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success("Thông tin sản phẩm đã được thay đổi")) : handleError(result);
    }
    
    @PostMapping("/{id}/prices")
    public ResponseEntity<ApiResponse<String>> changeProductPrice(@PathVariable("id") Integer id, @RequestBody AddProductPriceRequest addProductPriceRequest) {

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
}
