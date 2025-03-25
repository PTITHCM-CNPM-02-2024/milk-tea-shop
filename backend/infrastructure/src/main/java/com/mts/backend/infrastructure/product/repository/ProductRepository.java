package com.mts.backend.infrastructure.product.repository;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.product.Product;
import com.mts.backend.domain.product.entity.ProductPrice;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.identifier.ProductPriceId;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.repository.ICategoryRepository;
import com.mts.backend.domain.product.repository.IProductRepository;
import com.mts.backend.domain.product.repository.ISizeRepository;
import com.mts.backend.domain.product.value_object.ProductName;
import com.mts.backend.infrastructure.persistence.entity.CategoryEntity;
import com.mts.backend.infrastructure.persistence.entity.ProductEntity;
import com.mts.backend.infrastructure.persistence.entity.ProductPriceEntity;
import com.mts.backend.infrastructure.persistence.entity.ProductSizeEntity;
import com.mts.backend.infrastructure.product.jpa.JpaProductPriceRepository;
import com.mts.backend.infrastructure.product.jpa.JpaProductRepository;
import com.mts.backend.shared.exception.DomainException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProductRepository implements IProductRepository {
    private final ICategoryRepository categoryRepository;
    private final JpaProductRepository jpaProductRepository;
    private final JpaProductPriceRepository jpaProductPriceRepository;
    private final ISizeRepository sizeRepository;

    public ProductRepository(ICategoryRepository categoryRepository, JpaProductRepository jpaProductRepository, JpaProductPriceRepository jpaProductPriceRepository, ISizeRepository sizeRepository) {
        this.categoryRepository = categoryRepository;
        this.jpaProductRepository = jpaProductRepository;
        this.jpaProductPriceRepository = jpaProductPriceRepository;
        this.sizeRepository = sizeRepository;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Optional<Product> findById(ProductId id) {
        Objects.requireNonNull(id, "ID sản phẩm không được null");

        return jpaProductRepository.findById(id.getValue())
                .map(productEntity -> {
                    return new Product(
                            ProductId.of(productEntity.getId()),
                            ProductName.of(productEntity.getName()),
                            productEntity.getDescription(),
                            productEntity.getImagePath(),
                            productEntity.getIsAvailable(),
                            productEntity.getIsSignature(),
                            Optional.ofNullable(productEntity.getCategoryEntity()).map(CategoryEntity::getId).map(CategoryId::of).orElse(null),
                            findPricesByProductId(id),
                            productEntity.getUpdatedAt().orElse(null)
                    );
                });
    }

    /**
     * @param name 
     * @return
     */
    @Override
    public Optional<Product> findByName(ProductName name) {
        Objects.requireNonNull(name, "ProductName is required");
        
        return jpaProductRepository.findByName(name.getValue())
                .map(productEntity -> new Product(
                        ProductId.of(productEntity.getId()),
                        ProductName.of(productEntity.getName()),
                        productEntity.getDescription(),
                        productEntity.getImagePath(),
                        productEntity.getIsAvailable(),
                        productEntity.getIsSignature(),
                        Optional.ofNullable(productEntity.getCategoryEntity()).map(CategoryEntity::getId).map(CategoryId::of).orElse(null),
                        findPricesByProductId(ProductId.of(productEntity.getId())),
                        productEntity.getUpdatedAt().orElse(null)
                ));
    }

    /**
     * @return
     */
    @Override
    public List<Product> findAll() {
        Set<Product> products = new HashSet<>();
        
        jpaProductRepository.findAll().forEach(productEntity -> {
            Product product = new Product(
                    ProductId.of(productEntity.getId()),
                    ProductName.of(productEntity.getName()),
                    productEntity.getDescription(),
                    productEntity.getImagePath(),
                    productEntity.getIsAvailable(),
                    productEntity.getIsSignature(),
                    Optional.ofNullable(productEntity.getCategoryEntity()).map(CategoryEntity::getId).map(CategoryId::of).orElse(null),
                    findPricesByProductId(ProductId.of(productEntity.getId())),
                    productEntity.getUpdatedAt().orElse(null)
            );
            products.add(product);
        });
        
        return new ArrayList<>(products);
    }
    
    @Override
    public List<Product> findAllAvailable(){
        var products = findAll();
        
        return products.stream().filter(Product::isAvailable).toList();
    }
    
    
    @Override
    public List<Product> findAllSignature(){
        var products = findAll();
        
        return products.stream().filter(Product::isSignature).toList();
    }
    
    @Override
    public List<Product> findAllForSale(){
        var products = findAll();
        
        return products.stream().filter(Product::isOrdered).toList();
    }

    /**
     * @param name
     * @return
     */
    @Override
    public List<Product> findByNameContaining(String name) {
        return List.of();
    }

    /**
     * @param categoryId
     * @return
     */
    @Override
    public List<Product> findByCategoryId(CategoryId categoryId) {
        return List.of();
    }

    /**
     * @param isSignature
     * @return
     */
    @Override
    public List<Product> findBySignature(boolean isSignature) {
        List<Product> products = new ArrayList<>();

        jpaProductRepository.findByIsSignature(isSignature).forEach(productEntity -> {
            Product product = new Product(
                    ProductId.of(productEntity.getId()),
                    ProductName.of(productEntity.getName()),
                    productEntity.getDescription(),
                    productEntity.getImagePath(),
                    productEntity.getIsAvailable(),
                    productEntity.getIsSignature(),
                    Optional.ofNullable(productEntity.getCategoryEntity()).map(CategoryEntity::getId).map(CategoryId::of).orElse(null),
                    Set.of(),
                    productEntity.getUpdatedAt().orElse(null)
            );
            products.add(product);
        });

        return products;
    }

    /**
     * @param isAvailable
     * @return
     */
    @Override
    public List<Product> findByAvailable(boolean isAvailable) {
        List<Product> products = new ArrayList<>();

        jpaProductRepository.findByIsAvailable(isAvailable).forEach(productEntity -> {
            Product product = new Product(
                    ProductId.of(productEntity.getId()),
                    ProductName.of(productEntity.getName()),
                    productEntity.getDescription(),
                    productEntity.getImagePath(),
                    productEntity.getIsAvailable(),
                    productEntity.getIsSignature(),
                    Optional.ofNullable(productEntity.getCategoryEntity()).map(CategoryEntity::getId).map(CategoryId::of).orElse(null),
                    Set.of(),
                    productEntity.getUpdatedAt().orElse(null)
            );
            products.add(product);
        });

        return products;
    }

    /**
     * @param product
     */
    @Override
    public void delete(Product product) {

    }

    /**
     * @param id
     * @return
     */
    @Override
    public boolean existsById(ProductId id) {
        Objects.requireNonNull(id, "Product ID is required");

        return jpaProductRepository.existsById(id.getValue());
    }

    /**
     * @param name 
     * @return
     */
    @Override
    public boolean existsByName(ProductName name) {
        Objects.requireNonNull(name, "Product name is required");

        return jpaProductRepository.existsByName(name.getValue());
    }

    

    /**
     * Viết lại phương thức save.
     * Ý tưởng:
     * - Nếu sản phẩm không tồn tại thì tạo mới sản phẩm
     * - Nếu sản phẩm tồn tại thì cập nhật thông tin sản phẩm
     * @param product
     */
    @Override
    @Transactional
    public Product save(Product product) {
        Objects.requireNonNull(product, "Product is required");
        
        try {
            if (jpaProductRepository.existsById(product.getId().getValue())){
                return update(product);
            } else {
                return create(product);
            }
        }catch (Exception e){
            throw new RuntimeException("Không thể lưu thông tin sản phẩm: " + e.getMessage());
        }
        
    }

    /**
     * @param id 
     * @return
     */
    @Override
    public Optional<Product> findByPriceId(ProductPriceId id) {
        Objects.requireNonNull(id, "ProductPriceId is required");
        
        var productPriceEntity = jpaProductPriceRepository.findById(id.getValue());
        
        if (productPriceEntity.isEmpty()){
            return Optional.empty();
        }
        
        var productEntity = productPriceEntity.get().getProductEntity();
        
        return Optional.of(new Product(
                ProductId.of(productEntity.getId()),
                ProductName.of(productEntity.getName()),
                productEntity.getDescription(),
                productEntity.getImagePath(),
                productEntity.getIsAvailable(),
                productEntity.getIsSignature(),
                Optional.ofNullable(productEntity.getCategoryEntity()).map(CategoryEntity::getId).map(CategoryId::of).orElse(null),
                findPricesByProductId(ProductId.of(productEntity.getId())),
                productEntity.getUpdatedAt().orElse(LocalDateTime.now())
        ));
    }

    @Transactional
    protected Product create(Product product){
        Objects.requireNonNull(product, "Product is required");
        
        ProductEntity productEntity = ProductEntity.builder()
                .description(product.getDescription())
                .imagePath(product.getImagePath())
                .isAvailable(product.isAvailable())
                .isSignature(product.isSignature())
                .name(product.getName().getValue())
                .id(product.getId().getValue())
                .build();
        
        productEntity.setCreatedAt(product.getCreatedAt());
        productEntity.setUpdatedAt(product.getUpdatedAt());
        
        CategoryEntity categoryEntity = CategoryEntity.builder().id(product.getCategoryId().isPresent() ? product.getCategoryId().get().getValue() : null).build();
        
        productEntity.setCategoryEntity(categoryEntity);
        
        jpaProductRepository.insertProduct(productEntity);
        
        create(product.getId(), product.getPrices());
        
        return product;
    }
    

    @Transactional
    public Product update(Product product) {
        Objects.requireNonNull(product, "Sản phẩm không được null");
        
        ProductEntity productEntity = ProductEntity.builder()
                .description(product.getDescription())
                .imagePath(product.getImagePath())
                .isAvailable(product.isAvailable())
                .isSignature(product.isSignature())
                .name(product.getName().getValue())
                .id(product.getId().getValue())
                .build();
        
        CategoryEntity categoryEntity = CategoryEntity.builder().id(product.getCategoryId().isPresent() ? product.getCategoryId().get().getValue() : null).build();
        
        productEntity.setCategoryEntity(categoryEntity);
        
        jpaProductRepository.updateProduct(productEntity);
        
        update(product.getId(), product.getPrices());
        
        return product;
    }

    @Transactional
    protected void update(ProductId productId, Set<ProductPrice> domainPrices) {
        Objects.requireNonNull(productId, "Product ID is required");
        if (domainPrices == null ) {
            return;
        }

        // Get existing prices from database
        Set<ProductPriceEntity> dbPrices = jpaProductPriceRepository.findPricesByProductId(productId.getValue());

        // Map existing prices by size ID for easier comparison
        Map<Integer, ProductPriceEntity> dbPriceMap = new HashMap<>();
        for (ProductPriceEntity entity : dbPrices) {
            dbPriceMap.put(entity.getSize().getId(), entity);
        }

        // Map new prices by size ID
        Map<Integer, ProductPrice> domainPriceMap = new HashMap<>();
        for (ProductPrice price : domainPrices) {
            domainPriceMap.put(price.getSizeId().getValue(), price);
        }

        // Lists for different operations
        List<ProductPriceEntity> pricesToUpdate = new ArrayList<>();
        List<ProductPriceEntity> pricesToCreate = new ArrayList<>();
        List<Long> pricesToDelete = new ArrayList<>();

        // Find prices to update or delete
        for (ProductPriceEntity dbPrice : dbPrices) {
            int sizeId = dbPrice.getSize().getId();
            if (domainPriceMap.containsKey(sizeId)) {
                // Update existing price
                ProductPrice domainPrice = domainPriceMap.get(sizeId);
                dbPrice.setPrice(domainPrice.getPrice().getAmount());
                pricesToUpdate.add(dbPrice);
            } else {
                // Delete price not in domain set
                pricesToDelete.add(dbPrice.getId());
            }
        }

        // Find prices to create
        for (ProductPrice domainPrice : domainPrices) {
            int sizeId = domainPrice.getSizeId().getValue();
            if (!dbPriceMap.containsKey(sizeId)) {
                ProductPriceEntity priceEntity = ProductPriceEntity.builder()
                        .price(domainPrice.getPrice().getAmount())
                        .productEntity(ProductEntity.builder().id(productId.getValue()).build())
                        .size(ProductSizeEntity.builder().id(sizeId).build())
                        .build();
                pricesToCreate.add(priceEntity);
            }
        }

        // Execute price operations
        for (Long priceId : pricesToDelete) {
            jpaProductPriceRepository.deleteProductPrice(priceId);
        }

        for (ProductPriceEntity price : pricesToUpdate) {
            jpaProductPriceRepository.updateProductPrice(price);
        }

        for (ProductPriceEntity price : pricesToCreate) {
            jpaProductPriceRepository.insertProductPrice(price);
        }
    }

    private void create(ProductId id, Set<ProductPrice> prices) {
        if (prices == null || prices.isEmpty()) {
            return;
        }
        
        try {
            for (ProductPrice price : prices) {
                ProductPriceEntity priceEntity = ProductPriceEntity.builder()
                        .id(price.getId().getValue())
                        .price(price.getPrice().getAmount())
                        .productEntity(ProductEntity.builder().id(id.getValue()).build())
                        .size(ProductSizeEntity.builder().id(price.getSizeId().getValue()).build())
                        .build();
                jpaProductPriceRepository.insertProductPrice(priceEntity);
            }
        }catch (RuntimeException e){
            throw new DomainException("Không thể tạo giá sản phẩm: " + e.getMessage());
        }
    }
    
    private Set<ProductPrice> findPricesByProductId(ProductId productId) {
        Objects.requireNonNull(productId, "ProductID is required");
        Set<ProductPrice> prices = new HashSet<>();
        
        jpaProductPriceRepository.findPricesByProductId(productId.getValue()).forEach(priceEntity -> {
            ProductPrice price = new ProductPrice(
                    ProductPriceId.of(priceEntity.getId()),
                    productId,
                    ProductSizeId.of(priceEntity.getSize().getId()),
                    Money.of(priceEntity.getPrice()),
                    priceEntity.getUpdatedAt().orElse(LocalDateTime.now())
            );
            prices.add(price);
        });
        
        
        return prices;
    }
    
    @Override
    @Transactional
    public void createPrice(ProductId id, Set<ProductPrice> prices) {
        Objects.requireNonNull(id, "ProductID is required");
        Objects.requireNonNull(prices, "Prices is required");

        Set<ProductPriceEntity> newPrices = new HashSet<>();

        for (ProductPrice price : prices) {
            ProductPriceEntity priceEntity = ProductPriceEntity.builder()
                    .id(null)
                    .price(price.getPrice().getAmount())
                    .productEntity(ProductEntity.builder().id(id.getValue()).build())
                    .size(ProductSizeEntity.builder().id(price.getSizeId().getValue()).build())
                    .build();
            newPrices.add(priceEntity);
        }

        jpaProductPriceRepository.saveAll(newPrices);

    }
}
