package com.mts.backend.api.product.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.product.request.AddProductPriceRequest;
import com.mts.backend.api.product.request.CreateProductRequest;
import com.mts.backend.api.product.request.UpdateProductInformRequest;
import com.mts.backend.application.product.ProductCommandBus;
import com.mts.backend.application.product.ProductQueryBus;
import com.mts.backend.application.product.command.*;
import com.mts.backend.application.product.query.*;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.value_object.ProductName;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController implements IController {
    private final ProductCommandBus productCommandBus;
    private final ProductQueryBus productQueryBus;

    public ProductController(ProductCommandBus productCommandBus, ProductQueryBus productQueryBus) {
        this.productCommandBus = productCommandBus;
        this.productQueryBus = productQueryBus;
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createProduct(@RequestBody CreateProductRequest createProductRequest) {
        CreateProductCommand createProductCommand = CreateProductCommand.builder()
                .name(ProductName.builder().value(createProductRequest.getName()).build())
                .description(createProductRequest.getDescription())
                .categoryId(Objects.isNull(createProductRequest.getCategoryId()) ? null :
                        CategoryId.of(createProductRequest.getCategoryId()))
                .available(createProductRequest.getAvailable())
                .signature(createProductRequest.getSignature())
                .imagePath(createProductRequest.getImagePath())
                .build();

        for (var productPrice : createProductRequest.getPrices().entrySet()) {
            createProductCommand.getProductPrices().add(ProductPriceCommand.builder()
                    .sizeId(ProductSizeId.of(productPrice.getKey()))
                    .price(Money.builder().value(productPrice.getValue()).build())
                    .build());
        }

        var result = productCommandBus.dispatch(createProductCommand);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateProductInform(@PathVariable("id") Integer id, @RequestBody UpdateProductInformRequest updateProductInformRequest) {
        UpdateProductInformCommand updateProductInformCommand = UpdateProductInformCommand.builder()
                .productId(ProductId.of(id))
                .name(ProductName.builder().value(updateProductInformRequest.getName()).build())
                .description(updateProductInformRequest.getDescription())
                .categoryId(Objects.isNull(updateProductInformRequest.getCategoryId()) ? null :
                        CategoryId.of(updateProductInformRequest.getCategoryId()))
                .imagePath(updateProductInformRequest.getImagePath())
                .isAvailable(updateProductInformRequest.getAvailable())
                .isSignature(updateProductInformRequest.getSignature())
                .build();

        var result = productCommandBus.dispatch(updateProductInformCommand);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getProductDetail(@RequestParam(value = "page",
            defaultValue = "0") Integer page, @RequestParam(value = "size", defaultValue = "100") Integer size) {
        DefaultProductQuery getProductDetailCommand = DefaultProductQuery.builder()
                .page(page)
                .size(size)
                .build();

        var result = productQueryBus.dispatch(getProductDetailCommand);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getProductDetailById(@PathVariable("id") Integer id) {
        ProdByIdQuery getProductDetailCommand = ProdByIdQuery.builder()
                .id(ProductId.of(id))
                .build();

        var result = productQueryBus.dispatch(getProductDetailCommand);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @PostMapping("/{id}/prices")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createProductPrice(@PathVariable("id") Integer id,
                                                             @RequestBody AddProductPriceRequest addProductPriceRequest) {

        AddProductPriceCommand addProductPriceCommand = AddProductPriceCommand.builder()
                .productId(ProductId.of(id))
                .build();

        if (addProductPriceRequest.getProductPrices() != null) {
            for (var productPrice : addProductPriceRequest.getProductPrices()) {
                addProductPriceCommand.getProductPrices().add(ProductPriceCommand.builder()
                        .sizeId(ProductSizeId.of(productPrice.getSizeId()))
                        .price(Money.builder().value(productPrice.getPrice()).build())
                        .build());
            }
        }

        var result = productCommandBus.dispatch(addProductPriceCommand);

        return result.isSuccess() ? ResponseEntity.ok("Giá sản phẩm đã được thay đổi") : handleError(result);
    }

    @PutMapping("/{id}/prices")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateProductPrice(@PathVariable("id") Integer id,
                                                             @RequestBody AddProductPriceRequest addProductPriceRequest) {
        UpdateProductPriceCommand updateProductPriceCommand = UpdateProductPriceCommand.builder()
                .productId(ProductId.of(id))
                .build();

        if (addProductPriceRequest.getProductPrices() != null) {
            for (var productPrice : addProductPriceRequest.getProductPrices()) {
                updateProductPriceCommand.getProductPrices().add(ProductPriceCommand.builder()
                        .sizeId(ProductSizeId.of(productPrice.getSizeId()))
                        .price(Money.builder().value(productPrice.getPrice()).build())
                        .build());
            }
        }

        var result = productCommandBus.dispatch(updateProductPriceCommand);

        return result.isSuccess() ? ResponseEntity.ok("Giá sản phẩm đã được thay đổi") : handleError(result);
    }

    @DeleteMapping("/{id}/prices/{sizeId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteProductPrice(@PathVariable("id") Integer id,
                                                             @PathVariable("sizeId") Integer sizeId) {
        DeletePriceBySizeIdCommand deleteProductPriceCommand = DeletePriceBySizeIdCommand.builder()
                .productId(ProductId.of(id))
                .sizeId(ProductSizeId.of(sizeId))
                .build();

        var result = productCommandBus.dispatch(deleteProductPriceCommand);

        return result.isSuccess() ? ResponseEntity.ok("Giá sản phẩm đã được xóa") : handleError(result);
    }
    
    @GetMapping("/available-order/{available}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAvailableOrderProductDetail(@PathVariable("available") Boolean available){

        ProductForSaleQuery getProductDetailCommand = ProductForSaleQuery.builder()
                .availableOrder(available)
                .build();

        var result = productQueryBus.dispatch(getProductDetailCommand);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping("/topping")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getToppingProductDetail(@RequestParam(value = "page",
            defaultValue = "0") Integer page, @RequestParam(value = "size", defaultValue = "100") Integer size) {
        ToppingForSaleQuery getProductDetailCommand = ToppingForSaleQuery.builder()
                .page(page)
                .size(size)
                .isOrdered(Boolean.TRUE)
                .build();
        
        var result = productQueryBus.dispatch(getProductDetailCommand);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    @GetMapping("/search")
    public ResponseEntity<?> getSignatureProductDetail(@RequestParam(value = "isAvailableOrder",
            required = false, defaultValue = "true") Boolean isAvailableOrder, @RequestParam(value = "isSignature", defaultValue = "true") Boolean isSignature) {
        SignatureProductForSaleQuery getProductDetailCommand = SignatureProductForSaleQuery.builder()
                .availableOrder(isAvailableOrder)
                .isSignature(isSignature)
                .build();

        var result = productQueryBus.dispatch(getProductDetailCommand);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Integer id) {
        DeleteProductByIdCommand command = DeleteProductByIdCommand.builder()
                .id(ProductId.of(id))
                .build();

        var result = productCommandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    
}
