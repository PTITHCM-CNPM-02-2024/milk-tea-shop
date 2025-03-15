package com.mts.backend.api.product.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.product.request.CreateCategoryRequest;
import com.mts.backend.application.product.CategoryCommandBus;
import com.mts.backend.application.product.command.CreateCategoryCommand;
import com.mts.backend.application.product.command.UpdateCategoryCommand;
import com.mts.backend.application.product.handler.CreateCategoryCommandHandler;
import com.mts.backend.application.product.query.DefaultCategoryQuery;
import com.mts.backend.application.product.response.CategoryDetailResponse;
import com.mts.backend.application.product.response.CategoryQueryBus;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<ApiResponse<Integer>> createCategory(@RequestBody CreateCategoryRequest createCategoryRequest) {
        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .name(createCategoryRequest.getName())
                .description(createCategoryRequest.getDescription())
                .parentId(createCategoryRequest.getParentId())
                .build();
        
        var result = categoryCommandBus.dispatch(createCategoryCommand);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((int)result.getData())) : handleError(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Integer>> updateCategory(@PathVariable("id") Integer id, @RequestBody CreateCategoryRequest createCategoryRequest) {
        UpdateCategoryCommand createCategoryCommand = UpdateCategoryCommand.builder()
                .name(createCategoryRequest.getName())
                .description(createCategoryRequest.getDescription())
                .parentId(createCategoryRequest.getParentId())
                .id(id)
                .build();
        
        var result = categoryCommandBus.dispatch(createCategoryCommand);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((int)result.getData(), "Thông tin danh mục đã được cập nhật")) : handleError(result);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDetailResponse> >> getAllCategory() {
        
        DefaultCategoryQuery defaultCategoryQuery = DefaultCategoryQuery.builder().build();
        
        var result = categoryQueryBus.dispatch(defaultCategoryQuery);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((List<CategoryDetailResponse>)result.getData())) : handleError(result);
    }
    
    
}
