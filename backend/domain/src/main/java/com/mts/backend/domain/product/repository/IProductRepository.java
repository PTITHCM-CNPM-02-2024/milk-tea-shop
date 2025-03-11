package com.mts.backend.domain.product.repository;

import com.mts.backend.domain.product.Product;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.identifier.ProductId;

import java.util.List;
import java.util.Optional;

/**
 * Interface Repository cho Product Aggregate
 */
public interface IProductRepository {
    

    /**
     * Tìm sản phẩm theo ID
     */
    Optional<Product> findById(ProductId id);

    /**
     * Tìm tất cả sản phẩm
     */
    List<Product> findAll();

    /**
     * Tìm sản phẩm theo tên (tìm gần đúng)
     */
    List<Product> findByNameContaining(String name);

    /**
     * Tìm sản phẩm theo danh mục
     */
    List<Product> findByCategoryId(CategoryId categoryId);

    /**
     * Tìm sản phẩm đặc trưng (signature)
     */
    List<Product> findBySignature(boolean isSignature);

    /**
     * Tìm sản phẩm có sẵn (available)
     */
    List<Product> findByAvailable(boolean isAvailable);

    /**
     * Xóa sản phẩm
     */
    void delete(Product product);

    /**
     * Kiểm tra sản phẩm tồn tại theo ID
     */
    boolean existsById(ProductId id);
    
    
    /**
     * Tạo mới sản phẩm
     */
    Product create(Product product);
}
