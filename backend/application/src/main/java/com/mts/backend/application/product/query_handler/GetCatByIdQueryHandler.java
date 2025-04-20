package com.mts.backend.application.product.query_handler;

import com.mts.backend.application.product.query.CatByIdQuery;
import com.mts.backend.application.product.response.CategoryDetailResponse;
import com.mts.backend.application.product.response.ProductSummaryResponse;
import com.mts.backend.domain.product.Category;
import com.mts.backend.domain.product.jpa.JpaCategoryRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetCatByIdQueryHandler implements IQueryHandler<CatByIdQuery, CommandResult> {
    private final JpaCategoryRepository jpaCategoryRepository;
    
    public GetCatByIdQueryHandler(JpaCategoryRepository jpaCategoryRepository) {
        this.jpaCategoryRepository = jpaCategoryRepository;
    }

    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(CatByIdQuery query) {
        Objects.requireNonNull(query, "GetCatByIdQuery must not be null");
        
        var category = jpaCategoryRepository.findByIdFetch(query.getId().getValue());
        
        var categoryResponse = category.map(cat -> {
            return CategoryDetailResponse.builder()
                    .id(cat.getId())
                    .name(cat.getName().getValue())
                    .description(cat.getDescription().orElse(null))
                    .products(cat.getProducts().stream().map(p -> {
                        var productResponse = new ProductSummaryResponse();
                        productResponse.setId(p.getId());
                        productResponse.setName(p.getName().getValue());
                        productResponse.setDescription(p.getDescription());
                        productResponse.setImage_url(p.getImagePath());
                        productResponse.setSignature(p.getSignature());
                        productResponse.setCatId(p.getCategory().map(Category::getId).orElse(null));
                        productResponse.setAvailable(p.getAvailable());
                        return productResponse;
                    }).toList())
                    .build();
        }).orElseThrow(() -> new RuntimeException("Category not found"));
        
        return CommandResult.success(categoryResponse);
    }
}
