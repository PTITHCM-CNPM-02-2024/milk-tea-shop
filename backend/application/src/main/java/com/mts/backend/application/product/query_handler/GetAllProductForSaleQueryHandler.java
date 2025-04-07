package com.mts.backend.application.product.query_handler;

import com.mts.backend.application.product.query.ProductForSaleQuery;
import com.mts.backend.application.product.response.CategoryDetailResponse;
import com.mts.backend.application.product.response.ProductDetailResponse;
import com.mts.backend.domain.product.ProductEntity;
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
        List<ProductEntity> products = productRepository.findAllForSaleFetch(query.getIsOrdered());
        
        List<ProductDetailResponse> responses = products.stream().map(product -> {
            return ProductDetailResponse.builder()
                    .id(product.getId())
                    .description(product.getDescription())
                    .name(product.getName().getValue())
                    .image_url(product.getImagePath())
                    .signature(product.getSignature())
                    .category(product.getCategoryEntity().map(category -> CategoryDetailResponse.builder()
                            .id(category.getId())
                            .name(category.getName().getValue())
                            .build()).orElse(null))
                    .prices(product.getProductPriceEntities().stream().map(price -> {
                        return ProductDetailResponse.PriceDetail.builder()
                                .id(price.getId())
                                .price(price.getPrice().getValue())
                                .sizeId(price.getSize().getId())
                                .quantity(price.getSize().getQuantity().getValue())
                                .size(price.getSize().getName().getValue())
                                .currency("VND")
                                .unitName(price.getSize().getUnit().getName().getValue())
                                .unitSymbol(price.getSize().getUnit().getSymbol().getValue())
                                .build();
                    }).toList())
                    .build();
        }).toList();
        
        
        return CommandResult.success(responses);
    }
}
