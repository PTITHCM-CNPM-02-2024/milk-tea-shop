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
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.Objects;

@Tag(name = "Product Controller", description = "Product")
@RestController
@RequestMapping("/api/v1/products")
public class ProductController implements IController {
    private final ProductCommandBus productCommandBus;
    private final ProductQueryBus productQueryBus;

    public ProductController(ProductCommandBus productCommandBus, ProductQueryBus productQueryBus) {
        this.productCommandBus = productCommandBus;
        this.productQueryBus = productQueryBus;
    }

    @Operation(summary = "Tạo sản phẩm mới")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu đầu vào")
    })
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createProduct(@Parameter(description = "Thông tin sản phẩm", required = true) @RequestBody CreateProductRequest createProductRequest) {
        CreateProductCommand createProductCommand = CreateProductCommand.builder()
                .name(ProductName.of(createProductRequest.getName()))
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
                    .price(Money.of(productPrice.getValue()))
                    .build());
        }

        var result = productCommandBus.dispatch(createProductCommand);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Cập nhật thông tin sản phẩm")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateProductInform(@Parameter(description = "ID sản phẩm", required = true) @PathVariable("id") Integer id, @Parameter(description = "Thông tin cập nhật", required = true) @RequestBody UpdateProductInformRequest updateProductInformRequest) {
        UpdateProductInformCommand updateProductInformCommand = UpdateProductInformCommand.builder()
                .productId(ProductId.of(id))
                .name(ProductName.of(updateProductInformRequest.getName()))
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

    @Operation(summary = "Lấy danh sách sản phẩm")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllProduct(@Parameter(description = "Trang", required = false) @RequestParam(value = "page", defaultValue = "0") Integer page, @Parameter(description = "Kích thước trang", required = false) @RequestParam(value = "size", defaultValue = "100") Integer size) {
        DefaultProductQuery getProductDetailCommand = DefaultProductQuery.builder()
                .page(page)
                .size(size)
                .build();

        var result = productQueryBus.dispatch(getProductDetailCommand);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @Operation(summary = "Lấy chi tiết sản phẩm theo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getProductDetailById(@Parameter(description = "ID sản phẩm", required = true) @PathVariable("id") Integer id) {
        ProdByIdQuery getProductDetailCommand = ProdByIdQuery.builder()
                .id(ProductId.of(id))
                .build();

        var result = productQueryBus.dispatch(getProductDetailCommand);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Thêm giá cho sản phẩm")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @PostMapping("/{id}/prices")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createProductPrice(@Parameter(description = "ID sản phẩm", required = true) @PathVariable("id") Integer id,
                                                             @Parameter(description = "Thông tin giá", required = true) @RequestBody AddProductPriceRequest addProductPriceRequest) {

        AddProductPriceCommand addProductPriceCommand = AddProductPriceCommand.builder()
                .productId(ProductId.of(id))
                .build();

        if (addProductPriceRequest.getProductPrices() != null) {
            for (var productPrice : addProductPriceRequest.getProductPrices()) {
                addProductPriceCommand.getProductPrices().add(ProductPriceCommand.builder()
                        .sizeId(ProductSizeId.of(productPrice.getSizeId()))
                        .price(Money.of(productPrice.getPrice()))
                        .build());
            }
        }

        var result = productCommandBus.dispatch(addProductPriceCommand);

        return result.isSuccess() ? ResponseEntity.ok("Giá sản phẩm đã được thay đổi") : handleError(result);
    }

    @Operation(summary = "Cập nhật giá sản phẩm")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @PutMapping("/{id}/prices")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateProductPrice(@Parameter(description = "ID sản phẩm", required = true) @PathVariable("id") Integer id,
                                                             @Parameter(description = "Thông tin giá", required = true) @RequestBody AddProductPriceRequest addProductPriceRequest) {
        UpdateProductPriceCommand updateProductPriceCommand = UpdateProductPriceCommand.builder()
                .productId(ProductId.of(id))
                .build();

        if (addProductPriceRequest.getProductPrices() != null) {
            for (var productPrice : addProductPriceRequest.getProductPrices()) {
                updateProductPriceCommand.getProductPrices().add(ProductPriceCommand.builder()
                        .sizeId(ProductSizeId.of(productPrice.getSizeId()))
                        .price(Money.of(productPrice.getPrice()))
                        .build());
            }
        }

        var result = productCommandBus.dispatch(updateProductPriceCommand);

        return result.isSuccess() ? ResponseEntity.ok("Giá sản phẩm đã được thay đổi") : handleError(result);
    }

    @Operation(summary = "Xóa giá sản phẩm theo size")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @DeleteMapping("/{id}/prices/{sizeId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteProductPrice(@Parameter(description = "ID sản phẩm", required = true) @PathVariable("id") Integer id,
                                                             @Parameter(description = "ID size", required = true) @PathVariable("sizeId") Integer sizeId) {
        DeletePriceBySizeIdCommand deleteProductPriceCommand = DeletePriceBySizeIdCommand.builder()
                .productId(ProductId.of(id))
                .sizeId(ProductSizeId.of(sizeId))
                .build();

        var result = productCommandBus.dispatch(deleteProductPriceCommand);

        return result.isSuccess() ? ResponseEntity.ok("Giá sản phẩm đã được xóa") : handleError(result);
    }
    
    @Operation(summary = "Lấy sản phẩm có thể đặt hàng")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping("/available-order/{available}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAvailableOrderProductDetail(@Parameter(description = "Trạng thái có thể đặt hàng", required = true) @PathVariable("available") Boolean available){

        ProductForSaleQuery getProductDetailCommand = ProductForSaleQuery.builder()
                .availableOrder(available)
                .build();

        var result = productQueryBus.dispatch(getProductDetailCommand);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @Operation(summary = "Lấy sản phẩm topping")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping("/topping")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getToppingProductDetail(@Parameter(description = "Trang", required = false) @RequestParam(value = "page", defaultValue = "0") Integer page, @Parameter(description = "Kích thước trang", required = false) @RequestParam(value = "size", defaultValue = "100") Integer size) {
        ToppingForSaleQuery getProductDetailCommand = ToppingForSaleQuery.builder()
                .page(page)
                .size(size)
                .isOrdered(Boolean.TRUE)
                .build();
        
        var result = productQueryBus.dispatch(getProductDetailCommand);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @Operation(summary = "Tìm kiếm sản phẩm signature")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping("/search")
    public ResponseEntity<?> getSignatureProductDetail(@Parameter(description = "Có thể đặt hàng", required = false) @RequestParam(value = "isAvailableOrder",
            required = false, defaultValue = "true") Boolean isAvailableOrder, @Parameter(description = "Signature", required = false) @RequestParam(value = "isSignature", defaultValue = "true") Boolean isSignature) {
        SignatureProductForSaleQuery getProductDetailCommand = SignatureProductForSaleQuery.builder()
                .availableOrder(isAvailableOrder)
                .isSignature(isSignature)
                .build();

        var result = productQueryBus.dispatch(getProductDetailCommand);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Xóa sản phẩm")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteProduct(@Parameter(description = "ID sản phẩm", required = true) @PathVariable("id") Integer id) {
        DeleteProductByIdCommand command = DeleteProductByIdCommand.builder()
                .id(ProductId.of(id))
                .build();

        var result = productCommandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    
}
