package com.mts.backend.infrastructure.product.jpa;

import com.mts.backend.infrastructure.persistence.entity.ProductSizeEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaProductSizeRepository extends JpaRepository<ProductSizeEntity, Integer> {
    @Query("select p from ProductSizeEntity p where upper(p.name) = upper(?1)")
    Optional<ProductSizeEntity> findByName(@NonNull String name);
    

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO milk_tea_shop_prod.ProductSize (size_id, unit_id, name, quantity, description) VALUES (:#{#entity.id}, :#{#entity.unit.id}, :#{#entity.name}, :#{#entity.quantity}, :#{#entity.description})", nativeQuery = true)
    void insertProductSize(@Param("entity") @NonNull ProductSizeEntity entity);

    @Modifying
    @Transactional
    @Query(value = "UPDATE milk_tea_shop_prod.ProductSize SET " +
            "name = :#{#entity.name}, " +
            "quantity = :#{#entity.quantity}, " +
            "description = :#{#entity.description} " +
            "WHERE size_id = :#{#entity.id}", nativeQuery = true)
    void updateProductSize(@Param("entity") @NonNull ProductSizeEntity entity);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.ProductSize WHERE size_id = :id", nativeQuery = true)
    void deleteProductSize(@Param("id") @NonNull Integer id);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM milk_tea_shop_prod.ProductSize WHERE UPPER(name) = UPPER(:name) AND unit_id = :unitId)", nativeQuery = true)
    boolean existsByNameAndUnitId(@Param("name") @NonNull String name, @Param("unitId") @NonNull Integer unitId);

    @Query("select p from ProductSizeEntity p where p.name = ?1 and p.unit.id = ?2")
    Optional<ProductSizeEntity> findByNameAndUnit(@NonNull String name, @NonNull Integer id);


}
