package com.mts.backend.application.product.query_handler;

import com.mts.backend.application.product.query.DefaultProductQuery;
import com.mts.backend.application.product.response.ProductDetailResponse;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.repository.ICategoryRepository;
import com.mts.backend.domain.product.repository.IProductRepository;
import com.mts.backend.domain.product.repository.ISizeRepository;
import com.mts.backend.domain.product.repository.IUnitRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class GetAllProductQueryHandler implements IQueryHandler<DefaultProductQuery, CommandResult> {
    private final IProductRepository productRepository;
    private final ICategoryRepository categoryRepository;
    private final IUnitRepository unitRepository;
    private final ISizeRepository sizeRepository;
    
    public GetAllProductQueryHandler(IProductRepository productRepository, ICategoryRepository categoryRepository, IUnitRepository unitRepository, ISizeRepository sizeRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.unitRepository = unitRepository;
        this.sizeRepository = sizeRepository;
    }
    
    
    @Override
    public CommandResult handle(DefaultProductQuery query) {
        Objects.requireNonNull(query);
        
        var products = productRepository.findAll();
        
        List<ProductDetailResponse> responses = new ArrayList<>();
        
        products.forEach(product -> {
            ProductDetailResponse response = ProductDetailResponse.builder().id(product.getId().getValue()).description(product.getDescription()).name(product.getName().getValue()).image_url(product.getImagePath()).isSignature(product.isSignature()).build();

            if (product.getCategoryId().isPresent()) {
                Optional<CategoryId> categoryId = product.getCategoryId();
                response.setCategory(categoryRepository.findById(categoryId.get()).map(category -> category.getName().getValue()).orElse(""));
            }

            for (var price : product.getPrices()) {
                ProductDetailResponse.PriceDetail priceDetail = ProductDetailResponse.PriceDetail.builder().price(price.getPrice().getAmount()).currency("VND").build();

                var size = sizeRepository.findById(price.getSizeId()).orElseThrow(() -> new RuntimeException("Size not found"));

                priceDetail.setSize(size.getName().getValue());
                priceDetail.setQuantity(size.getQuantity().getValue());

                var unit = unitRepository.findById(size.getUnitOfMeasure()).orElseThrow(() -> new RuntimeException("Unit not found"));

                priceDetail.setUnitName(unit.getName().getValue());
                priceDetail.setUnitSymbol(unit.getSymbol().getValue());
                
                response.getPrices().add(priceDetail);
            }
            
            responses.add(response);
        });
        
        return CommandResult.success(responses);
    }
    
}
