package com.mts.backend.application.product.query_handler;

import com.mts.backend.application.product.query.SignatureProductQuery;
import com.mts.backend.application.product.response.ProductDetailResponse;
import com.mts.backend.domain.product.Product;
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

@Service
public class GetAllSignatureProductQueryHandler implements IQueryHandler<SignatureProductQuery, CommandResult> {
    
    private final IProductRepository productRepository;
    private final ISizeRepository sizeRepository;
    private final ICategoryRepository categoryRepository;
    private final IUnitRepository unitRepository;

    public GetAllSignatureProductQueryHandler(IProductRepository productRepository, ISizeRepository sizeRepository, ICategoryRepository categoryRepository, IUnitRepository unitRepository) {
        this.productRepository = productRepository;
        this.sizeRepository = sizeRepository;
        this.categoryRepository = categoryRepository;
        this.unitRepository = unitRepository;
    }


    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(SignatureProductQuery query) {
        
        Objects.requireNonNull(query, "SignatureProductQuery is required");
        
        var products = getProducts(query.isSignature(), query.isOrdered());
        
        List<ProductDetailResponse> responses = new ArrayList<>();
        
        products.forEach(product -> {
            ProductDetailResponse response = ProductDetailResponse.builder().id(product.getId().getValue()).description(product.getDescription()).name(product.getName().getValue()).image_url(product.getImagePath()).isSignature(product.isSignature()).build();

            if (product.getCategoryId().isPresent()) {
                var categoryId = product.getCategoryId().get();
                response.setCategory(categoryRepository.findById(categoryId).map(category -> category.getName().getValue()).orElse(""));
            }

            for (var price : product.getPrices()) {
                ProductDetailResponse.PriceDetail priceDetail = ProductDetailResponse.PriceDetail.builder().price(price.getPrice().getValue()).currency("VND").build();

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
    
    private List<Product> getProducts(boolean isSignature, boolean isOrdered) {
        return productRepository.findAll().stream().filter(product -> product.isSignature() == isSignature && product.isAvailable() && product.isOrdered() == isOrdered).toList();
    }
}
