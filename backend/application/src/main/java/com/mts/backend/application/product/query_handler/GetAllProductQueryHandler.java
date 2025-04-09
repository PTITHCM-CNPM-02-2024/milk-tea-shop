package com.mts.backend.application.product.query_handler;

import com.mts.backend.application.product.query.DefaultProductQuery;
import com.mts.backend.application.product.response.CategoryDetailResponse;
import com.mts.backend.application.product.response.ProductDetailResponse;
import com.mts.backend.application.product.response.ProductSummaryResponse;
import com.mts.backend.domain.product.ProductEntity;
import com.mts.backend.domain.product.jpa.JpaProductRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetAllProductQueryHandler implements IQueryHandler<DefaultProductQuery, CommandResult> {
    private final JpaProductRepository productRepository;
    
    public GetAllProductQueryHandler(JpaProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    
    @Override
    public CommandResult handle(DefaultProductQuery query) {
        Objects.requireNonNull(query);
        
        Page<ProductEntity> products = productRepository.findAllFetch(Pageable.ofSize(query.getSize()).withPage(query.getPage()));
        
        Page<ProductSummaryResponse> responses = products.map(product -> {
            ProductSummaryResponse response =
                    ProductSummaryResponse.builder().id(product.getId()).description(product.getDescription()).name(product.getName().getValue()).image_url(product.getImagePath()).signature(product.getSignature()).build();

            product.getCategoryEntity().ifPresent(category -> {
                response.setCatId(category.getId());
            });

            return response;
        });

        return CommandResult.success(responses);
    }
    
}
