package com.mts.backend.api.product.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.product.request.CreateCategoryRequest;
import com.mts.backend.application.product.CategoryCommandBus;
import com.mts.backend.application.product.command.CreateCategoryCommand;
import com.mts.backend.application.product.command.DeleteCatByIdCommand;
import com.mts.backend.application.product.command.UpdateCategoryCommand;
import com.mts.backend.application.product.query.CatByIdQuery;
import com.mts.backend.application.product.query.DefaultCategoryQuery;
import com.mts.backend.application.product.query.ProdByCatIdQuery;
import com.mts.backend.application.product.CategoryQueryBus;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.value_object.CategoryName;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "Category Controller", description = "Category")
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController implements IController {

    private final CategoryCommandBus categoryCommandBus;

    private final CategoryQueryBus categoryQueryBus;

    public CategoryController(CategoryCommandBus categoryCommandBus, CategoryQueryBus categoryQueryBus) {
        this.categoryCommandBus = categoryCommandBus;
        this.categoryQueryBus = categoryQueryBus;
    }

    @Operation(summary = "Tạo danh mục mới")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu đầu vào")
    })
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createCategory(@Parameter(description = "Thông tin danh mục", required = true) @RequestBody CreateCategoryRequest createCategoryRequest) {
        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .name(CategoryName.of(createCategoryRequest.getName()))
                .description(createCategoryRequest.getDescription())
                .build();

        var result = categoryCommandBus.dispatch(createCategoryCommand);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Cập nhật danh mục")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy danh mục")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateCategory(@Parameter(description = "ID danh mục", required = true) @PathVariable("id") Integer id,
                                            @Parameter(description = "Thông tin cập nhật", required = true) @RequestBody CreateCategoryRequest createCategoryRequest) {
        UpdateCategoryCommand createCategoryCommand = UpdateCategoryCommand.builder()
                .id(CategoryId.of(id))
                .name(CategoryName.of(createCategoryRequest.getName()))
                .description(createCategoryRequest.getDescription())
                .build();   
        var result = categoryCommandBus.dispatch(createCategoryCommand);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Lấy danh sách danh mục")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllCategory(@Parameter(description = "Trang", required = false) @RequestParam(value = "page", defaultValue = "0") Integer page,
                                            @Parameter(description = "Kích thước trang", required = false) @RequestParam(value = "size", defaultValue = "10") Integer size) {

        DefaultCategoryQuery defaultCategoryQuery = DefaultCategoryQuery.builder()
                .page(page)
                .size(size)
                .build();

        var result = categoryQueryBus.dispatch(defaultCategoryQuery);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    
    @Operation(summary = "Lấy danh mục theo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy danh mục")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCategoryById(@Parameter(description = "ID danh mục", required = true) @PathVariable("id") Integer id) {
        Objects.requireNonNull(id, "Category ID must not be null");

        var result = categoryQueryBus.dispatch(CatByIdQuery.builder().id(CategoryId.of(id)).build());

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    
    @Operation(summary = "Lấy sản phẩm theo danh mục")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping("/{id}/products")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCategoryProducts(@Parameter(description = "ID danh mục", required = true) @PathVariable("id") Integer id,
                                                 @Parameter(description = "Có thể đặt hàng", required = false) @RequestParam(value = "availableOrdered", required = false) Boolean availableOrdered,
                                                 @Parameter(description = "Trang", required = false) @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                 @Parameter(description = "Kích thước trang", required = false) @RequestParam(value = "size", defaultValue = "10") Integer size) {
        ProdByCatIdQuery query = ProdByCatIdQuery.builder()
                .id(CategoryId.of(id))
                .availableOrder(availableOrdered)
                .page(page)
                .size(size)
                .build();

        var result = categoryQueryBus.dispatch(query);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);

    }

    @Operation(summary = "Xóa danh mục")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy danh mục")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteCategory(@Parameter(description = "ID danh mục", required = true) @PathVariable("id") Integer id) {
        DeleteCatByIdCommand command = DeleteCatByIdCommand.builder()
                .id(CategoryId.of(id))
                .build();
        var result = categoryCommandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
