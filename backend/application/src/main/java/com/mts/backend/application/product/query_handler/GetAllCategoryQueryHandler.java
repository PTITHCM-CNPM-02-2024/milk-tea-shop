package com.mts.backend.application.product.query_handler;

import com.mts.backend.application.product.query.DefaultCategoryQuery;
import com.mts.backend.application.product.response.CategoryDetailResponse;
import com.mts.backend.domain.product.repository.ICategoryRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllCategoryQueryHandler implements IQueryHandler<DefaultCategoryQuery, CommandResult> {
    
    private final ICategoryRepository categoryRepository;
    
    public GetAllCategoryQueryHandler(ICategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    @Override
    public CommandResult handle(DefaultCategoryQuery query) {
        Objects.requireNonNull(query);
        
        var categories = categoryRepository.findAll();
        
        List<CategoryDetailResponse> responses = new ArrayList<>();
        
        categories.forEach(category -> {
            CategoryDetailResponse response = CategoryDetailResponse.builder().id(category.getId().getValue()).name(category.getName().getValue()).build();
            
            if (category.getParentId().isPresent()) {
                var parentId = category.getParentId().get();
                
                CategoryDetailResponse parent = categoryRepository.findById(parentId).map(parentCategory -> CategoryDetailResponse.builder().id(parentCategory.getId().getValue()).name(parentCategory.getName().getValue()).build()).orElse(null);
                
                response.setParent(parent);
            }
            
            responses.add(response);
        });
        
        return CommandResult.success(responses);
    }
}
