package com.mts.backend.application.product.query_handler;

import com.mts.backend.application.product.query.DefaultCategoryQuery;
import com.mts.backend.application.product.response.CategoryDetailResponse;
import com.mts.backend.domain.product.jpa.JpaCategoryRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllCategoryQueryHandler implements IQueryHandler<DefaultCategoryQuery, CommandResult> {
    
    private final JpaCategoryRepository categoryRepository;
    
    public GetAllCategoryQueryHandler(JpaCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    @Override
    @Transactional
    public CommandResult handle(DefaultCategoryQuery query) {
        Objects.requireNonNull(query);
        
        var categories = categoryRepository.findAllWithJoinFetch(PageRequest.ofSize(query.getSize()).withPage(query.getPage()));
        
        List<CategoryDetailResponse> responses = new ArrayList<>();
        
        categories.forEach(category -> {
            CategoryDetailResponse response = CategoryDetailResponse.builder().id(category.getId()).name(category.getName().getValue()).build();
            
            category.getParentCategoryEntity().ifPresent(parent -> {
                response.setParent(CategoryDetailResponse.builder().id(parent.getId()).name(parent.getName().getValue()).build());
            });
            
            responses.add(response);
        });
        
        return CommandResult.success(responses);
    }
}
