package com.mts.backend.domain.product.repository;

import com.mts.backend.domain.product.Product;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.value_object.ProductName;

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
     * Tìm sản phẩm theo tên
     */
    Optional<Product> findByName(ProductName name);

    /**
     * Tìm tất cả sản phẩm
     */
    List<Product> findAll();

    /**
     * Tìm tất cả sản phẩm đang có sẵn
     */
    List<Product> findAllAvailable();

    List<Product> findAllSignature();

    /**
     * Tìm tất cả sản phẩm có thể bán
     */
    List<Product> findAllForSale();



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
     * Kiểm tra sản phẩm tồn tại theo tên
     */
    boolean existsByName(ProductName name);
    

    /**
     * Cập nhật thông tin sản phẩm
     *
     * @param product
     */
    Product save(Product product);

    void updateInform(Product product);

}
