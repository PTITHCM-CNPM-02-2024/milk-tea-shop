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
    public ResponseEntity<?> createCategory(@RequestBody CreateCategoryRequest createCategoryRequest) {
        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .name(CategoryName.builder().value(createCategoryRequest.getName()).build())
                .description(createCategoryRequest.getDescription())
                .build();

        var result = categoryCommandBus.dispatch(createCategoryCommand);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable("id") Integer id,
                                            @RequestBody CreateCategoryRequest createCategoryRequest) {
        UpdateCategoryCommand createCategoryCommand = UpdateCategoryCommand.builder()
                .id(CategoryId.of(id))
                .name(CategoryName.builder().value(createCategoryRequest.getName()).build())
                .description(createCategoryRequest.getDescription())
                .build();   
        var result = categoryCommandBus.dispatch(createCategoryCommand);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @GetMapping
    public ResponseEntity<?> getAllCategory() {

        DefaultCategoryQuery defaultCategoryQuery = DefaultCategoryQuery.builder()
                .build();

        var result = categoryQueryBus.dispatch(defaultCategoryQuery);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
