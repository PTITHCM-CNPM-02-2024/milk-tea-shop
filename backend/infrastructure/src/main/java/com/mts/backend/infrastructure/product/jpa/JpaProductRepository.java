package com.mts.backend.infrastructure.product.jpa;

import com.mts.backend.infrastructure.persistence.entity.ProductEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, Integer> {
    @Query(value = "SELECT * FROM milk_tea_shop_prod.Product p WHERE UPPER(p.name) = UPPER(:#{#name})", nativeQuery = true)
    Optional<ProductEntity> findByName(@Param("name") @NonNull String name);

    @Query(value = "SELECT * FROM milk_tea_shop_prod.Product p WHERE p.is_available = :#{#isAvailable}", nativeQuery = true)
    List<ProductEntity> findByIsAvailable(@Param("isAvailable") boolean isAvailable);

    @Query(value = "SELECT * FROM milk_tea_shop_prod.Product p WHERE p.is_signature = :#{#isSignature}", nativeQuery = true)
    List<ProductEntity> findByIsSignature(@Param("isSignature") @NonNull boolean isSignature);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM milk_tea_shop_prod.Product p WHERE p.name = :#{#name})", nativeQuery = true)
    boolean existsByName(@Param("name") @NonNull String name);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO milk_tea_shop_prod.Product (name, description, category_id, is_available, is_signature, image_path) " +
            "VALUES (:#{#entity.name}, :#{#entity.description}, :#{#entity.categoryEntity?.id}, :#{#entity.isAvailable}, " +
            ":#{#entity.isSignature}, :#{#entity.imagePath})", nativeQuery = true)
    void insertProduct(@Param("entity") ProductEntity entity);

    @Modifying
    @Transactional
    @Query(value = "UPDATE milk_tea_shop_prod.Product SET " +
            "name = :#{#entity.name}, " +
            "description = :#{#entity.description}, " +
            "category_id = :#{#entity.categoryEntity?.id}, " +
            "is_available = :#{#entity.isAvailable}, " +
            "is_signature = :#{#entity.isSignature}, " +
            "image_path = :#{#entity.imagePath} " +
            "WHERE product_id = :#{#entity.id}", nativeQuery = true)
    void updateProduct(@Param("entity") ProductEntity entity);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.Product WHERE product_id = :#{#productId}", nativeQuery = true)
    void deleteProduct(@Param("productId") Integer productId);
}