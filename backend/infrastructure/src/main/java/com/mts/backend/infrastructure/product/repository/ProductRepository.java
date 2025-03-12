package com.mts.backend.infrastructure.product.repository;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.product.Category;
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
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
                            productEntity.getCreatedAt().orElse(null),
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
                        productEntity.getCreatedAt().orElse(null),
                        productEntity.getUpdatedAt().orElse(null)
                ));
    }

    /**
     * @return
     */
    @Override
    public List<Product> findAll() {
        return List.of();
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
                    productEntity.getCreatedAt().orElse(null),
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
                    productEntity.getCreatedAt().orElse(null),
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

    @Override
    @Transactional
    public Product create(Product product) {
        Objects.requireNonNull(product, "Product is required");
        
        ProductEntity productEntity = ProductEntity.builder().description(product.getDescription())
                .imagePath(product.getImagePath())
                .isAvailable(product.isAvailable())
                .isSignature(product.isSignature())
                .name(product.getName().getValue())
                .id(null)
                .build();
        
        if (product.getCategoryId().isPresent()){
            CategoryEntity categoryEntity = CategoryEntity.builder().id(product.getCategoryId().get().getValue()).build();
            productEntity.setCategoryEntity(categoryEntity);
        }
        
        jpaProductRepository.save(productEntity);
        
        return new Product(
                ProductId.of(productEntity.getId()),
                product.getName(),
                product.getDescription(),
                product.getImagePath(),
                product.isAvailable(),
                product.isSignature(),
                product.getCategoryId().orElse(null),
                null,
                productEntity.getCreatedAt().orElse(null),
                productEntity.getUpdatedAt().orElse(null)
        );
        
    }

    /**
     * @param product
     */
    @Override
    @Transactional
    public void save(Product product) {
        Objects.requireNonNull(product, "Product is required");
        
        ProductEntity productEntity = ProductEntity.builder()
                .description(product.getDescription())
                .imagePath(product.getImagePath())
                .isAvailable(product.isAvailable())
                .isSignature(product.isSignature())
                .name(product.getName().getValue())
                .id(product.getId().getValue())
                .build();
        
        if (product.getCategoryId().isPresent()){
            CategoryEntity categoryEntity = CategoryEntity.builder().id(product.getCategoryId().get().getValue()).build();
            productEntity.setCategoryEntity(categoryEntity);
        }
        
        jpaProductRepository.save(productEntity);
        
        for (ProductPrice price : product.getPrices()) {
            ProductPriceEntity priceEntity = ProductPriceEntity.builder()
                    .id(price.getId().getValue())
                    .price(price.getPrice().getAmount())
                    .productEntity(productEntity)
                    .size(ProductSizeEntity.builder().id(price.getSizeId().getValue()).build())
                    .build();
            jpaProductPriceRepository.save(priceEntity);
        }
        
    }

    @Override
    @Transactional
    public void updateInform(Product product) {
        Objects.requireNonNull(product, "Sản phẩm không được null");

        try {

            verifyUniqueProductName(product.getName());

            CategoryEntity existingCategoryEntity = getCategoryEntityIfExists(product.getCategoryId());

            ProductEntity productEntity = jpaProductRepository.findById(product.getId().getValue())
                    .orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại"));

            productEntity.setName(product.getName().getValue());
            productEntity.setDescription(product.getDescription());
            productEntity.setImagePath(product.getImagePath());
            productEntity.setIsAvailable(product.isAvailable());
            productEntity.setIsSignature(product.isSignature());
            productEntity.setCategoryEntity(existingCategoryEntity);
            productEntity.setUpdatedAt(product.getUpdatedAt());
            jpaProductRepository.save(productEntity);

        } catch (DomainException | DuplicateException | NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException("Không thể cập nhật thông tin sản phẩm: " + e.getMessage());
        }
    }

    private void verifyUniqueProductName(ProductName name) {
        jpaProductRepository.findByName(name.getValue())
                .ifPresent(p -> {
                    ProductName existingName = ProductName.of(p.getName());
                    if (existingName.equals(name)) {
                        throw new DuplicateException("Tên sản phẩm \"" + name.getValue() + "\" đã tồn tại");
                    }
                });
    }

    private CategoryEntity getCategoryEntityIfExists(Optional<CategoryId> categoryId) {
        if (categoryId.isEmpty()) {
            return null;
        }

        Category category = categoryRepository.findById(categoryId.get())
                .orElseThrow(() -> new NotFoundException("Danh mục không tồn tại"));

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(category.getId().getValue());
        return categoryEntity;
    }

    private void savePrices(Set<ProductPrice> prices, ProductEntity productEntity) {
        if (prices == null || prices.isEmpty()) {
            return;
        }

        prices.forEach(price -> {
            ProductPriceEntity priceEntity = new ProductPriceEntity();
            priceEntity.setProductEntity(productEntity);
            priceEntity.setPrice(price.getPrice().getAmount());

            ProductSizeEntity sizeEntity = sizeRepository.findById(price.getSizeId()).map(size -> {
                ProductSizeEntity sizeEntity1 = new ProductSizeEntity();
                sizeEntity1.setId(size.getId().getValue());
                return sizeEntity1;
            }).orElseThrow(() -> new NotFoundException("Kích thước sản phẩm không tồn tại"));

            priceEntity.setSize(sizeEntity);
            jpaProductPriceRepository.save(priceEntity);
        });
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
                    priceEntity.getCreatedAt().orElse(null),
                    priceEntity.getUpdatedAt().orElse(null)
            );
            prices.add(price);
        });
        
        
        return prices;
    }
    
    @Override
    @Transactional
    public void addPrice(ProductId id, Set<ProductPrice> prices) {
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
