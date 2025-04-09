package com.mts.backend.application.product.query_handler;

import com.mts.backend.application.product.query.ProdByCatIdQuery;
import com.mts.backend.application.product.response.CategoryDetailResponse;
import com.mts.backend.application.product.response.CategorySummaryResponse;
import com.mts.backend.application.product.response.ProductDetailResponse;
import com.mts.backend.application.product.response.ProductSummaryResponse;
import com.mts.backend.domain.product.CategoryEntity;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.jpa.JpaProductRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllProdByCatIdQueryHandler implements IQueryHandler<ProdByCatIdQuery, CommandResult> {
    
    private final JpaProductRepository productRepository;
    
    public GetAllProdByCatIdQueryHandler(JpaProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public CommandResult handle(ProdByCatIdQuery query) {
        
        Objects.requireNonNull(query, "ProdByCatIdQuery must not be null");
        
        if (query.getId().isPresent() && (query.getId().get().getValue() == 1)){
            
            var topping = productRepository.findAllByCategoryEntity_Id(1).stream()
                    .filter(product -> !product.isOrdered())
                    .toList();

            List<ProductDetailResponse> responses = topping.stream().map(product -> {
                var productResponse = ProductDetailResponse.builder()
                        .id(product.getId())
                        .description(product.getDescription())
                        .name(product.getName().getValue())
                        .image_url(product.getImagePath())
                        .signature(product.getSignature())
                        .build();
                
                product.getCategoryEntity().ifPresent(category -> {
                    productResponse.setCategory(CategoryDetailResponse.builder()
                            .id(category.getId())
                            .name(category.getName().getValue())
                            .build());
                });
                
                for (var price : product.getProductPriceEntities()) {
                    ProductDetailResponse.PriceDetail priceDetail = ProductDetailResponse.PriceDetail.builder()
                            .price(price.getPrice().getValue())
                            .currency("VND")
                            .build();
                    
                    priceDetail.setId(price.getId());
                    priceDetail.setSizeId(price.getSize().getId());
                    priceDetail.setQuantity(price.getSize().getQuantity().getValue());
                    priceDetail.setSize(price.getSize().getName().getValue());
                    priceDetail.setCurrency("VND");
                    priceDetail.setUnitName(price.getSize().getUnit().getName().getValue());
                    productResponse.getPrices().add(priceDetail);
                }
                
                return productResponse;
            }
            ).toList();
            
            return CommandResult.success(responses);
        }
        
        
        var getAllProdByCatId = productRepository.findAllByCatIdAndOrderedFetch(query.getId().map(CategoryId::getValue).orElse(null),
                query.getAvailableOrder().orElse(null), Pageable.ofSize(query.getSize()).withPage(query.getPage()));   
        
        var productResponses = getAllProdByCatId.map(product -> {
            return ProductSummaryResponse.builder()
                    .id(product.getId())
                    .name(product.getName().getValue())
                    .description(product.getDescription())
                    .image_url(product.getImagePath())
                    .signature(product.getSignature())
                    .minPrice(BigDecimal.valueOf(product.getProductPriceEntities().stream().mapToInt(price -> price.getPrice().getValue().intValue()).min().orElse(0)))
                    .catId(product.getCategoryEntity().map(CategoryEntity::getId).orElse(null))
                    .build();
        });
        
        return CommandResult.success(productResponses);
    }
    
    
}
