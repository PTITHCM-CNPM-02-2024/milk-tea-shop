package com.mts.backend.infrastructure.product.jpa;

import com.mts.backend.infrastructure.persistence.entity.ProductEntity;
import com.mts.backend.infrastructure.persistence.entity.ProductPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Repository
public interface JpaProductPriceRepository extends JpaRepository<ProductPriceEntity, Long>, JpaSpecificationExecutor<ProductPriceEntity> {
    @Query("select p from ProductPriceEntity p where p.productEntity.id = ?1")
    Set<ProductPriceEntity> findPricesByProductId(@NonNull Integer id);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO milk_tea_shop_prod.ProductPrice (product_price_id, product_id, size_id, price)" +
            "VALUES (:#{#entity.id}, :#{#entity.productEntity.id}, :#{#entity.size.id}, :#{#entity.price})",
            nativeQuery = true)
    void insertProductPrice(@Param("entity") ProductPriceEntity entity);
    

    @Modifying
    @Transactional
    @Query(value = "UPDATE milk_tea_shop_prod.ProductPrice SET " +
            "price = :#{#entity.price} " +
            "WHERE product_price_id = :#{#entity.id}",
            nativeQuery = true)
    void updateProductPrice(@Param("entity") ProductPriceEntity entity);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.ProductPrice WHERE product_price_id = :id",
            nativeQuery = true)
    void deleteProductPrice(@Param("id") Long id);
}