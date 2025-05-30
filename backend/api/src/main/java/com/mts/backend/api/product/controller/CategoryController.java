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
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createCategory(@RequestBody CreateCategoryRequest createCategoryRequest) {
        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .name(CategoryName.builder().value(createCategoryRequest.getName()).build())
                .description(createCategoryRequest.getDescription())
                .build();

        var result = categoryCommandBus.dispatch(createCategoryCommand);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllCategory(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                            @RequestParam(value = "size", defaultValue = "10") Integer size) {

        DefaultCategoryQuery defaultCategoryQuery = DefaultCategoryQuery.builder()
                .page(page)
                .size(size)
                .build();

        var result = categoryQueryBus.dispatch(defaultCategoryQuery);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCategoryById(@PathVariable("id") Integer id) {
        Objects.requireNonNull(id, "Category ID must not be null");

        var result = categoryQueryBus.dispatch(CatByIdQuery.builder().id(CategoryId.of(id)).build());

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    
    @GetMapping("/{id}/products")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCategoryProducts(@PathVariable("id") Integer id,
                                                 @RequestParam(value = "availableOrdered", required = false) Boolean availableOrdered,
                                                 @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                 @RequestParam(value = "size", defaultValue = "10") Integer size) {
        ProdByCatIdQuery query = ProdByCatIdQuery.builder()
                .id(CategoryId.of(id))
                .availableOrder(availableOrdered)
                .page(page)
                .size(size)
                .build();

        var result = categoryQueryBus.dispatch(query);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") Integer id) {
        DeleteCatByIdCommand command = DeleteCatByIdCommand.builder()
                .id(CategoryId.of(id))
                .build();
        var result = categoryCommandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
