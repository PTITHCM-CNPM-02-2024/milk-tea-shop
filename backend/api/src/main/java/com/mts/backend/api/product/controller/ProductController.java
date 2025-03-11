package com.mts.backend.api.product.controller;

import com.mts.backend.api.product.request.CreateProductRequest;
import com.mts.backend.application.product.ProductCommandBus;
import com.mts.backend.application.product.command.CreateProductCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductCommandBus productCommandBus;
    
    public ProductController(ProductCommandBus productCommandBus) {
        this.productCommandBus = productCommandBus;
    }
    
    @PostMapping
    public ResponseEntity<Integer> createProduct(@RequestBody CreateProductRequest createProductRequest) {
        CreateProductCommand createProductCommand = CreateProductCommand.builder()
                .name(createProductRequest.getName())
                .description(createProductRequest.getDescription())
                .categoryId(createProductRequest.getCategoryId())
                .available(createProductRequest.isAvailable())
                .signature(createProductRequest.isSignature())
                .imagePath(createProductRequest.getImagePath())
                .build();
        
        for (CreateProductRequest.ProductPriceRequest productPriceRequest : createProductRequest.getPrices()) {
            createProductCommand.getPrices().ifPresent(prices -> prices.put(productPriceRequest.getSizeIdValue(), productPriceRequest.getPriceAmount()));
        }
        
        var result = productCommandBus.dispatch(createProductCommand);
        
        return result.isSuccess() ? ResponseEntity.ok((int)result.getSuccessObject()) : ResponseEntity.badRequest().build();
        
    }
}
