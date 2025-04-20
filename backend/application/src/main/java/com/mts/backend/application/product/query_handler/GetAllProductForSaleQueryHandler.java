package com.mts.backend.application.product.query_handler;

import com.mts.backend.application.product.query.ProductForSaleQuery;
import com.mts.backend.application.product.response.ProductSummaryResponse;
import com.mts.backend.domain.product.Product;
import com.mts.backend.domain.product.jpa.JpaProductRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class GetAllProductForSaleQueryHandler implements IQueryHandler<ProductForSaleQuery, CommandResult> {
    private final JpaProductRepository productRepository;
    
    public GetAllProductForSaleQueryHandler(JpaProductRepository productRepository){
        this.productRepository = productRepository;
    }
    
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(ProductForSaleQuery query) {
        Objects.requireNonNull(query, "OrderedProductQuery is required");
        List<Product> products = productRepository.findAllForSaleFetch(query.getAvailableOrder());
        
        List<ProductSummaryResponse> responses = products.stream().map(product -> {
            
            var productResponse = ProductSummaryResponse.builder()
                    .id(product.getId())
                    .description(product.getDescription())
                    .name(product.getName().getValue())
                    .image_url(product.getImagePath())
                    .signature(product.getSignature())
                    .minPrice(product.getMinPrice().getValue())
                    .build();
            
            product.getCategory().ifPresent(category -> {
                productResponse.setCatId(category.getId());
            });
            
            return productResponse;
        }).toList();
        
        return CommandResult.success(responses);
    }
}
