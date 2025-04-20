package com.mts.backend.application.product.query_handler;

import com.mts.backend.application.product.query.ProdByIdQuery;
import com.mts.backend.application.product.response.CategoryDetailResponse;
import com.mts.backend.application.product.response.ProductDetailResponse;
import com.mts.backend.domain.product.Product;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.jpa.JpaProductRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

@Service
public class GetProductByIdQueryHandler implements IQueryHandler<ProdByIdQuery, CommandResult> {
    private final JpaProductRepository jpaProductRepository;
    
    public GetProductByIdQueryHandler(JpaProductRepository jpaProductRepository) {
        this.jpaProductRepository = jpaProductRepository;
    }
    
    
    @Override
    public CommandResult handle(ProdByIdQuery query) {
        var result = mustExist(query.getId());
        
        
        var response = ProductDetailResponse.builder()
                .id(result.getId())
                .description(result.getDescription())
                .name(result.getName().getValue())
                .image_url(result.getImagePath())
                .signature(result.getSignature())
                .available(result.getAvailable())
                .category(result.getCategory().map(category -> CategoryDetailResponse.builder()
                        .id(category.getId())
                        .name(category.getName().getValue())
                        .build()).orElse(null))
                .prices(result.getProductPriceEntities().stream().map(price -> ProductDetailResponse.PriceDetail.builder()
                        .id(price.getId())
                        .price(price.getPrice().getValue())
                        .sizeId(price.getSize().getId())
                        .quantity(price.getSize().getQuantity().getValue())
                        .size(price.getSize().getName().getValue())
                        .unitId(price.getSize().getUnit().getId())
                        .currency("VND")
                        .unitName(price.getSize().getUnit().getName().getValue())
                        .unitSymbol(price.getSize().getUnit().getSymbol().getValue())
                        .build()).toList())
                .build();
        
        return CommandResult.success(response);
    }
    
    
    private Product mustExist(ProductId productId) {
        return jpaProductRepository.findByIdFetch(productId.getValue()).orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm với id: " + productId.getValue()));
    }
    
    
}
