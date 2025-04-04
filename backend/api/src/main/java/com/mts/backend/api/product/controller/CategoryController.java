package com.mts.backend.api.product.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.product.request.CreateCategoryRequest;
import com.mts.backend.application.product.CategoryCommandBus;
import com.mts.backend.application.product.command.CreateCategoryCommand;
import com.mts.backend.application.product.command.UpdateCategoryCommand;
import com.mts.backend.application.product.query.DefaultCategoryQuery;
import com.mts.backend.application.product.response.CategoryQueryBus;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.value_object.CategoryName;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController implements IController {

    private final CategoryCommandBus categoryCommandBus;

    private final CategoryQueryBus categoryQueryBus;

    public CategoryController(CategoryCommandBus categoryCommandBus, CategoryQueryBus categoryQueryBus) {
        this.categoryCommandBus = categoryCommandBus;
        this.categoryQueryBus = categoryQueryBus;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createCategory(@RequestBody CreateCategoryRequest createCategoryRequest) {
        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .name(CategoryName.builder().value(createCategoryRequest.getName()).build())
                .description(createCategoryRequest.getDescription())
                .parentId(Objects.isNull(createCategoryRequest.getParentId()) ? null :
                        CategoryId.of(createCategoryRequest.getParentId()))
                .build();

        var result = categoryCommandBus.dispatch(createCategoryCommand);

        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateCategory(@PathVariable("id") Integer id,
                                                         @RequestBody CreateCategoryRequest createCategoryRequest) {
        UpdateCategoryCommand createCategoryCommand = UpdateCategoryCommand.builder()
                .id(CategoryId.of(id))
                .name(CategoryName.builder().value(createCategoryRequest.getName()).build())
                .description(createCategoryRequest.getDescription())
                .parentId(Objects.isNull(createCategoryRequest.getParentId()) ? null :
                        CategoryId.of(createCategoryRequest.getParentId()))
                .build();
        var result = categoryCommandBus.dispatch(createCategoryCommand);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData(), "Thông tin danh mục đã " +
                "được cập nhật")) : handleError(result);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllCategory(@RequestParam(value = "size",
                                                                 defaultValue = "50") int size,
                                                         @RequestParam(value = "page", defaultValue = "0") int page) {

        DefaultCategoryQuery defaultCategoryQuery = DefaultCategoryQuery.builder()
                .page(page)
                .size(size)
                .build();

        var result = categoryQueryBus.dispatch(defaultCategoryQuery);

        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
    }


}
