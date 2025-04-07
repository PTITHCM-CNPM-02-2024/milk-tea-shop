package com.mts.backend.application.product.query_handler;

import com.mts.backend.application.product.query.DefaultProductQuery;
import com.mts.backend.application.product.response.CategoryDetailResponse;
import com.mts.backend.application.product.response.ProductDetailResponse;
import com.mts.backend.domain.product.ProductEntity;
import com.mts.backend.domain.product.jpa.JpaProductRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        
        Page<ProductEntity> products = productRepository.findAllWithDetails(Pageable.ofSize(query.getSize()).withPage(query.getPage()));
        
        Page<ProductDetailResponse> responses = products.map(product -> {
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

            return response;
        });

        return CommandResult.success(responses);
    }
    
}
