package com.mts.backend.api.product.controller;

import com.mts.backend.api.product.request.CreateCategoryRequest;
import com.mts.backend.application.product.command.CreateCategoryCommand;
import com.mts.backend.application.product.handler.CreateCategoryCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    
    private final CreateCategoryCommandHandler categoryCommandBus;
    
    public CategoryController(CreateCategoryCommandHandler categoryCommandBus) {
        this.categoryCommandBus = categoryCommandBus;
    }
    
    @PostMapping
    public ResponseEntity<Integer> createCategory(@RequestBody CreateCategoryRequest createCategoryRequest) {
        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .name(createCategoryRequest.getName())
                .description(createCategoryRequest.getDescription())
                .parentId(createCategoryRequest.getParentId())
                .build();
        
        var result = categoryCommandBus.handle(createCategoryCommand);
        
        return result.isSuccess() ? ResponseEntity.ok((int)result.getSuccessObject()) : ResponseEntity.badRequest().build();
        
    }
    
}
