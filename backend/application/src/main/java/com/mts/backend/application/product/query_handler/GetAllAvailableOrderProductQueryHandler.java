package com.mts.backend.application.product.query_handler;

import com.mts.backend.application.product.query.ProductForSaleQuery;
import com.mts.backend.application.product.response.CategoryDetailResponse;
import com.mts.backend.application.product.response.ProductDetailResponse;
import com.mts.backend.domain.product.ProductEntity;
import com.mts.backend.domain.product.jpa.JpaProductRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllAvailableOrderProductQueryHandler implements IQueryHandler<ProductForSaleQuery, CommandResult> {
    private final JpaProductRepository productRepository;
    
    public GetAllAvailableOrderProductQueryHandler(JpaProductRepository productRepository){
        this.productRepository = productRepository;
    }
    
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(ProductForSaleQuery query) {
        Objects.requireNonNull(query, "OrderedProductQuery is required");
        List<ProductEntity> products = null;
        
        if (query.getIsOrdered()){
            products = productRepository.findAllWithDetails().stream()
                    .filter(ProductEntity::isOrdered)
                    .toList();
        } else {
            products = productRepository.findAllWithDetails().stream()
                    .filter(product -> !product.isOrdered())
                    .toList();
        }
   

        List<ProductDetailResponse> responses = new ArrayList<>();
        
        products.forEach(product -> {
            ProductDetailResponse response =
                    ProductDetailResponse.builder().id(product.getId()).description(product.getDescription()).name(product.getName().getValue()).image_url(product.getImagePath()).signature(product.getSignature()).build();

            product.getCategoryEntity().ifPresent(category -> {
                response.setCategory(CategoryDetailResponse.builder().id(category.getId()).name(category.getName().getValue()).build());
            });

            for (var price : product.getProductPriceEntities()) {
                ProductDetailResponse.PriceDetail priceDetail = ProductDetailResponse.PriceDetail.builder().price(price.getPrice().getValue()).currency("VND").build();

                priceDetail.setSizeId(price.getSize().getId());
                priceDetail.setQuantity(price.getSize().getQuantity().getValue());
                priceDetail.setSize(price.getSize().getName().getValue());
                priceDetail.setCurrency("VND");
                priceDetail.setUnitName(price.getSize().getUnit().getName().getValue());
                priceDetail.setUnitSymbol(price.getSize().getUnit().getSymbol().getValue());

                response.getPrices().add(priceDetail);
            }

            responses.add(response);
        });
        
        return CommandResult.success(responses);
    }
}
