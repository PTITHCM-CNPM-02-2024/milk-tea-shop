package com.mts.backend.api.product.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.product.request.AddProductPriceRequest;
import com.mts.backend.api.product.request.UpdateProductInformRequest;
import com.mts.backend.api.product.request.CreateProductRequest;
import com.mts.backend.application.product.ProductCommandBus;
import com.mts.backend.application.product.ProductQueryBus;
import com.mts.backend.application.product.command.*;
import com.mts.backend.application.product.query.DefaultProductQuery;
import com.mts.backend.application.product.query.AvailableOrderProductQuery;
import com.mts.backend.application.product.query.SignatureProductQuery;
import com.mts.backend.application.product.response.ProductDetailResponse;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController implements IController {
    private final ProductCommandBus productCommandBus;
    private final ProductQueryBus productQueryBus;
    
    public ProductController(ProductCommandBus productCommandBus , ProductQueryBus productQueryBus) {
        this.productCommandBus = productCommandBus;
        this.productQueryBus = productQueryBus;
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
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDetailResponse>>> getProductDetail() {
        DefaultProductQuery getProductDetailCommand = DefaultProductQuery.builder()
                .build();
        
        var result = productQueryBus.dispatch(getProductDetailCommand);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((List<ProductDetailResponse>)result.getData())) : handleError(result);
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
    
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<ProductDetailResponse>>> getUnavailableOrderProductDetail() {
        AvailableOrderProductQuery getProductDetailCommand = AvailableOrderProductQuery.builder()
                .isOrdered(true)
                .build();
        
        var result = productQueryBus.dispatch(getProductDetailCommand);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((List<ProductDetailResponse>)result.getData())) : handleError(result);
    }
    
    @GetMapping("/not-available")
    public ResponseEntity<ApiResponse<List<ProductDetailResponse>>> getAvailableOrderProductDetail() {
        
        AvailableOrderProductQuery getProductDetailCommand = AvailableOrderProductQuery.builder()
                .isOrdered(false)
                .build();
        
        var result = productQueryBus.dispatch(getProductDetailCommand);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((List<ProductDetailResponse>)result.getData())) : handleError(result);
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductDetailResponse>>> getSignatureProductDetail(@RequestParam(value = "isAvailableOrder", required = false, defaultValue = "true") Boolean isAvailableOrder, @RequestParam(value = "isSignature", defaultValue = "true") Boolean isSignature) {
        SignatureProductQuery getProductDetailCommand = SignatureProductQuery.builder()
                .isSignature(isSignature)
                .build();
        getProductDetailCommand.setOrdered(isAvailableOrder);
        
        var result = productQueryBus.dispatch(getProductDetailCommand);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((List<ProductDetailResponse>)result.getData())) : handleError(result);
    }
}
