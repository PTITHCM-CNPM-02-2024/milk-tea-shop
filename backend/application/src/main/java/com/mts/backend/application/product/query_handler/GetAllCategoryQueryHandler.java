package com.mts.backend.application.product.query_handler;

import com.mts.backend.application.product.query.DefaultCategoryQuery;
import com.mts.backend.application.product.response.CategoryDetailResponse;
import com.mts.backend.application.product.response.CategorySummaryResponse;
import com.mts.backend.domain.product.CategoryEntity;
import com.mts.backend.domain.product.jpa.JpaCategoryRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        
        Page<CategoryEntity> categories = categoryRepository.findAll(Pageable.ofSize(query.getSize()).withPage(query.getPage()));
        
        Page<CategorySummaryResponse> responses = categories.map(category -> {
            return CategoryDetailResponse.builder()
                    .id(category.getId())
                    .name(category.getName().getValue())
                    .description(category.getDescription().orElse(null))
                    .build();
        });
        
        return CommandResult.success(responses);
    }
}
